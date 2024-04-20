package chilo.tech.avis.securite;

import chilo.tech.avis.entities.Jwt;
import chilo.tech.avis.entities.RefreshToken;
import chilo.tech.avis.repository.JwtRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import chilo.tech.avis.entities.Utilisateur;
import chilo.tech.avis.sevices.UtilisateurService;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.time.Instant;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
@Slf4j
@Transactional
@AllArgsConstructor
@Service
public class JwtService {
    public static final String BEARER = "bearer";
    public static final String REFRESH = "refresh";
    public static final String TOKEN_INVALID = "Le jeton de rafraîchissement est invalide ou a expiré.";
    private JwtRepository jwtRepository;
    private UtilisateurService utilisateurService;

    public Jwt tokenByValeur(String valeur) {
        return this.jwtRepository.findByValeurAndDesactiverAndExpired(
                valeur,
                false,
                false
        ).orElseThrow(() -> new RuntimeException("Token invalide ou inconnu"));
    }

    public Map<String, String> generate(String username) {
        Utilisateur utilisateur = this.utilisateurService.loadUserByUsername(username);

        this.disableTokens(utilisateur);
        final Map<String, String> jwtMap = new java.util.HashMap<>(this.generateJwt(utilisateur));

        RefreshToken refreshToken = RefreshToken.builder()
                .valeur(UUID.randomUUID().toString())
                .expire(false)
                .creation(Instant.now())
                .expiration(Instant.now().plusMillis(30 *60 *1000))
                .build();

        final Jwt jwt = Jwt
                .builder()
                .valeur(jwtMap.get(BEARER))
                .desactiver(false)
                .expired(false)
                .utilisateur(utilisateur)
                .refreshToken(refreshToken)
                .build();

        this.jwtRepository.save(jwt);
        jwtMap.put(REFRESH,  refreshToken.getValeur());
        return jwtMap;
    }

    private Date getExpirationDateFromToken(String token) {
        return this.getClaim(token, Claims::getExpiration);
    }

    private Map<String, String> generateJwt(Utilisateur utilisateur) {
        final Instant currentTime = Instant.now();
        final Instant expirationTime = currentTime.plusSeconds(60);
        final Map<String, Object> claims = Map.of(
                "nom", utilisateur.getNom(),
                Claims.EXPIRATION, Date.from(expirationTime),
                    Claims.SUBJECT, utilisateur.getEmail()
        );

        String bearer = Jwts.builder()
                .setIssuedAt(Date.from(currentTime))
                .setExpiration(Date.from(expirationTime))
                .setSubject(utilisateur.getEmail())
                .setClaims(claims)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
        return Map.of(BEARER,bearer);
    }
    private Key getKey(){
        String ENCRYPTION_KEY = "5b3fc907ae092dea6a880595c87e81c2519e1496b9053dd05d145bf7d531e1c9";
        final byte[] decoder = Decoders.BASE64.decode(ENCRYPTION_KEY);

        return Keys.hmacShaKeyFor(decoder );
    }

    public String extractUsername(String token) {
        return  this.getClaim(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate = this.getClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }



    private <T> T getClaim(String token, Function<Claims, T> function) {

        Claims claims = getAllClaims(token);
        return  function.apply(claims);
    }

    private Claims getAllClaims(String token) {

        return  Jwts.parserBuilder()
                .setSigningKey(this.getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Map<String, String> refreshToken(Map<String, String> refreshTokenRequest) {
        final Jwt jwt = this.jwtRepository.findByRefreshToken(refreshTokenRequest.get(REFRESH)).orElseThrow(() -> new RuntimeException(TOKEN_INVALID));
        if(jwt.getRefreshToken().isExpire() || jwt.getRefreshToken().getExpiration().isBefore(Instant.now())) {
            throw new RuntimeException(TOKEN_INVALID);
        }
        this.disableTokens(jwt.getUtilisateur());
        return this.generate(jwt.getUtilisateur().getEmail());
    }
    private void disableTokens(Utilisateur utilisateur) {
        final List<Jwt> jwtList = this.jwtRepository.findUtilisateur(utilisateur.getEmail()).peek(
                jwt -> {
                    jwt.setExpired(true);
                    jwt.setDesactiver(true);
                }
        ).collect(Collectors.toList());

        this.jwtRepository.saveAll(jwtList);
    }

    public void deconnexion() {
        Utilisateur utilisateur = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
         Jwt jwt =  this.jwtRepository.findUtilisateurValidToken(utilisateur.getEmail(),
                false,
                false).orElseThrow(() -> new RuntimeException("TOKEN_VALIDE"));
         jwt.setExpired(true);
         jwt.setDesactiver(true);

        this.jwtRepository.save(jwt);
    }
    @Scheduled(cron = "* * * * *")
    public void removeUselessJwt() {
        log.info("Suppression des token à {}", Instant.now());
        this.jwtRepository.deleteAllByExpiredAndDesactiver(true, true);
    }
}

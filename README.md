# Java Jwt Example


for authorization
즉 권한이 있음을 보여주는 토큰

header, payload, verify signature로 구성됨
* header - 암호화에 사용된 알고리즘, 해당 토큰을 설명하는 값  
* payload - 실제 데이터(사용자 이름등) claims라고도 불린다.  
* verify signature - 감춰진 비밀코드와 암호화 알고리즘을 이용해 header와 payload를 암호화한 값이다.  
즉 만약 내가 아닌 누군가가 header혹은 payload를 변경한다고 해도 비밀코드로 암호화를 진행하지 못하므로
verify signature의 값과 `header + "." + payload`값을 암호화 해본값과 비교하면 유효한 데이터인지 확인가능하다.   
  
이러한 JWT는 인증과정에서 서버가 클라이언트에게 주며
모든 요청에서 클라이언트가 JWT를 첨부하여 권한을 보여주는것이다.  

각 header, payload, key는 base64url로 인코딩되어 사용된다.  

[jjwt java docs](https://javadoc.io/doc/io.jsonwebtoken/jjwt-api/latest/index.html)
# 1. 추가방법
```
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId> <!-- or jjwt-gson if Gson is preferred -->
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
```

# 2. JWS 만들기
1. header만든다.
2. claim만든다
3. 위의 둘을 특정 알고리즘으로 인코딩한다.
4. secret key를 이용해 3번의 정보로 verify signature를 만든다.
5. 3번과 4번의 결과물을 이용해 jwt문자열 완성


1. `Jwts.builder()`로 `JwtBuilder`생성
2. `JwtBuilder`로 header, claim 추가
3. 대칭키의 비밀키 혹은 일반 `SecretKey`로 signature 생성
4. `compact()`로 문자열로 변환

## 2.1 Header 구성하기
헤더에는 kid(key id), metadata, 암호화 알고리즘등이 들어간다.  
이때 `alg`, `zip`파라미터는 수정하지 않는다. builder가 알아서 사용된 방법을 보고 구성해준다.  

`JwtBuilder.setHeaderParam(String name, Object value)`를 이용할 수 있다.
```
String jws = Jwts.builder()
    .setHeaderParam("kid", "myKeyId")
```

`JwtBuilder.setHeader(Map<String, Object> header)`도 사용가능
```
Map<String,Object> header = getMyHeaderMap(); //implement me
String jws = Jwts.builder()
    .setHeader(header)
```

## 2.2 Claim 구성하기
claim은 여러 파라미터가 있어서 `setExpiration()`등 여러 메소드가 있는데 이는 필요할때 찾아쓰기  
  
__custom claim__
```
String jws = Jwts.Builder()
    .claim("username", "wonbin")
    ...
```  

`setClaims(Map<String, Object> claim)`사용
```
Map<String,Object> claims = getMyClaimsMap(); //implement me
String jws = Jwts.builder()
    .setClaims(claims)
```

## 2.3 key만들기
`java.security.Key`또는 `javax.crypto.SecretKey`를 사용하거나 대칭키를 사용한다. SecretKey만 정리했다.  
`io.jsonwebtoken.security.Keys`를 사용하면 `HMAC-SHA`알고리즘을 적용시킬 수 있다.  
```
String password = "이건 인코딩되지 않은 문자열의 경우";
SecretKey key = Keys.hmacShaKeyFor(password.getBytes(StandardCharsets.UTF_8));

//인코딩된 경우
byte[] encoded = Base64.getEncoder().encode(password.getBytes(StandardCharsets.UTF_8));
SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(encoded));
```
key는 생성될때 `base64url`로 인코딩되기 때문에 raw data로 키를 생성해줘야한다.

## 2.4 signature 추가하고 문자열로 만들기
`JwtBuilder.signWith(SecretKey key)`과 `compact()`사용

```
String jws = Jwts.builder()
    ....
    .signWith(key)
    .compact();
```

# 3. JWT 읽기

## 3.1 일반적 방법
1. `Jwts.parserBuilder()`로 `JwtParserBuilder`객체 생성
2. 암호화시 사용했던 `SecretKey`혹은 복호화에 필요한 `public key`를 이용해 jws문자열의 signature와 맞는지 비교하도록 설정한다.(다를경우 예외발생)
3. `build()`호출하여 `JwtParser`객체 생성
4. `parseClaimsJws(jwsString)`에 jws 문자열 넘겨주어 `Jws<Claims>`로 파싱

```
Jws<Claims> jws;
try{
    jws = Jwts.parserBuilder()      //(1)
        .setSigningKey(secret_key or public_key)//(2) signature key 설정
        .build()                    //(3)
        .parserClaimsJws(jwsString);//(4) 파싱 하기 -> signature 안맞으면 예외 발생
}catch(JwtException ex){
    // signature 부분이 key와 맞지 않는경우
}
```

## 3.2 특정 claim을 포함하도록 제한

## 3.3 시간정보로 필터링


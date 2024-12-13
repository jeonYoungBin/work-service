1. api 정의
   1) GET /api/v1/book/sign(회원 가입)
    - request body
      {
        "userId":"user1",
        "password":"test1",
        "userName":"홍길동",
        "age": 20
      }
    - response 
      {
        "token": "token"    
      }      
   
   2) GET /api/v1/book/login(로그인)
    - request body
      {
        "userId":"user1",
        "password":"test1"
      }
    - response 
      {
        "token": "token"    
      }   
   
   3) POST /api/v1/book/{bookId}/views(작품 조회)
     - pathvariable : bookId
     - response
       {
         "viewHistoryId": 1    
       } 
   
   4) GET /api/v1/book/{bookId}/views(작품 조회 이력)
     - pathvariable : bookId
     
     - response
     [
       {
         "viewHistoryId": 1,
         "name": "홍길동",
         "age": 17,
         "viewedAt": "2024-12-09T14:14:57.826979"
       }, 
       {
         "viewHistoryId": 2,
         "name": "임꺽정",
         "age": 17,
         "viewedAt": "2024-12-09T15:09:44.673054"
       }
     ]
   
   5) GET /api/v1/book/view/popular(인기 작품 조회)
     - response
     [
        {
           "bookId": 1,
           "title": "A",
           "viewCount": 2
        },
        {
           "bookId": 2,
           "title": "B",
           "viewCount": 1
        }
    ]
   
   6) POST /api/v1/book/{bookId}/purchase(작품 구매)
     - pathvariable : bookId
     - response
        {   
            "purchaseHistoryId": 2
        }
     
   7) GET /api/v1/book/purchases/popular(구매 인기 작품 조회)
     - response
     [
        {   
            "bookId": 2,
            "title": "B",
            "purchaseCount": 1
        }
     ]
     
   8) DELETE /api/v1/book/{bookId}(작품 및 이력 삭제)
     - pathvariable : bookId 
   
   9) POST /api/v1/book(작품 등록)
    - response
        {   
            "bookId": 2
        }
                        
 2. api 실행방법
    1) 회원 가입
      - 회원 가입시 아이디, 패스워드, 이름, 나이 필수
    2) 작품 정보는 어플리캐이션 실행시 저장하게끔 되어있음
      - 어플리캐이션 재실행시, 
          - applicaton.yml -> ddl-auto none 설정
          - initData book init 블럭 주석처리
      - 추가 작품 등록을 진행 할경우 POST /api/v1/book 호출 
    3) 모든 api는 1)에 나온 토큰으로 access 가능(로그인 api 제외)
      - postman -> Authorization -> type Bearer Token 설정 후 토큰 입력
    4) 작품 조회
      -  POST /api/v1/book/{bookId}/views
    5) 작품 조회 이력 
      - GET /api/v1/book/{bookId}/views 
 3. 개발한 코드에 대해 참고할 수 있는 내용
   - 스프링 시큐리티 적용(JWT 포함)
   - 로컬캐쉬 적용
   - JPA 적용
 4. 고려했던 상황과 해결방안에 대한 내용

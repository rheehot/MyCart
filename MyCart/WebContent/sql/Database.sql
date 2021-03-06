drop table userList purge;

create table userList(
	userID varchar2(20) is null primary key,
	userPassword varchar2(20),
	userName varchar2(20),
	userGender varchar2(20),
	userEmail varchar2(20),
	userAdmin number(2),
	userJoinDate DATE
	--Primary key 를 못써먹는다 ㅡㅡ.... 도대체 왜 --> 삽입이 두번 된다..
)

select * from userList;

insert into userList
values('테스터1', '1111', '홍길동', '남', 'tester01@gmail.com')

update userList set userID='tester01' where userName='홍길동'

alter table userList
add(
	userAdmin number(2)
)

update userList set userAdmin='00' where userName='홍길동'

insert into userList
values('admin', '9999', '관리자', '여', 'admin@myCart.com', '09', null)

alter table userList
add(
	userJoinDate date
)

delete from userList where userID = 'test1'

ALTER TABLE userList DROP PRIMARY KEY DROP INDEX;

ALTER TABLE userList ADD CONSTRAINT user_ID PRIMARY KEY (userID)

drop table userMyTableBoard purge;

-- tagging, memo 기능이 추가되었으면 한다.
create table userMyTableBoard(
	boardType varchar2(5),
	boardID number(10),
	boardTitle varchar2(100),
	boardPrice number(30),
	boardEa number(10),
	boardMemo varchar2(2000),
	boardSellerLink varchar2(2000),
	boardTag varchar2(50),
	boardDate DATE,
	boardAvailable number(1), --글 삭제 구분 
	boardUserID varchar2(20) -- references userList(userID); 이걸 session.param 으로 받아서 join 기준으로 삼으면 되겠다 --제약을 인라인으로 정의할 경우 foreign key 가 필요없다고 함
);

alter table userMyTableBoard add boardType varchar2(5)
--modifiedDate 넣어서 사용빈도수 역산

select * from userMyTableBoard;

update userMyTableBoard set boardAvailable = 1 where boardUserID = 'tester7' and boardID =1 

select * from userMyTableBoard where boardUserID = 'tester88';

ALTER TABLE userMyTableBoard modify boardTitle null; 
ALTER TABLE userMyTableBoard modify boardPrice null; 
ALTER TABLE userMyTableBoard modify boardUserID null; 

--getListPriceSum 을 위함
ALTER TABLE userMyTableBoard add (boardPrice*boardEa as boardTotPrice number(12)); 
ALTER TABLE userMyTableBoard drop column boardTotPrice; 
select boardPrice*boardEa as priceSum from userMyTableBoard where boardUserID = 'tester3' and boardAvailable = 1

select sum(priceSum) from (select boardPrice*boardEa as priceSum from userMyTableBoard where boardUserID = 'tester3' and boardAvailable = 1)
select sum(priceSum) / sum(boardEa) from (select boardPrice*boardEa as priceSum, boardEa from userMyTableBoard where boardUserID = 'tester3' and boardAvailable = 1)

--리스트 합계 및 평균가 추출 query
select sum(boardPrice) from userMyTableBoard where boardUserID = 'tester7' and boardAvailable = 1 group by boardUserID
select sum(boardPrice) as priceSum from userMyTableBoard where boardUserID = 'tester3' and boardAvailable = 1 group by boardUserID
select sum(boardPrice*boardEa) as priceSum from userMyTableBoard where boardUserID = ? and boardAvailable = 1 group by boardUserID
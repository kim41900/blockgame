import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class blockgame {
   
   static class MyFrame extends JFrame{ //Jframe을 상속
      //constant 상수부호를 만듬
      static int BALL_WIDTH = 20; //볼넓이
      static int BALL_HEIGHT = 20; //볼 높이
      static int BLOCK_ROWS = 5; //블록의 줄(행) 수
      static int BLOCK_COLUMS = 10; //블록의 컬럼(열) 수
      static int BLOCK_WIDTH = 40; //하나의 블록의 넓이
      static int BLOCK_HEIGHT = 20; //블록의 높이
      static int BLOCK_GAP = 3; //블록과 블록사이의 간격
      static int BAR_WIDTH = 80; //사용자가 움직일 바의 넓이
      static int BAR_HEIGHT = 20; //사용자가 움직일 바의 높이
      static int CANVAS_WIDTH = 400+(BLOCK_GAP*BLOCK_COLUMS)-BLOCK_GAP; //캠버스의 넓이(BLOCK_WITHS*BLOCK_COLUMS=400+(BLOCK_GAP*(컬럼의 갯수만큼)-맨오른쪽 갭)
      static int CANVAS_HEIGHT = 600;
      
      //variable 변수지정
      static MyPanel myPanel = null;//패널(그림판)을 만들어 객체생성
      static int score = 0; //점수를 표시하는 변수 생성
      static Timer timer = null; //
      static Block[][] blocks = new Block[BLOCK_ROWS][BLOCK_COLUMS];//block이라는 클래스를 만들어 2차배열로 만듬   
      static Bar bar = new Bar(); //바의 정보를 가지고 있는 클래스 객체
      static Ball ball = new Ball(); //볼의 정보를 가진 클래스 객체
      static int barXTarget = bar.x; //barXTarget이라는 변수를 지정,bar가 x라는 변수를 가지고 있다.(Target Value-interpolation(한번에 많은 값을 보관))
      static int dir = 0; //볼이 움직이는 방향 4가지  0:Up-Right 1:Down-Right 2:Up-Left 3:Down-Left
      static int ballSpeed = 5; //볼의 속도
      static boolean isGameFinish = false;  
      
      static class Ball{ //Ball정보를 가진 클래스 생성 
         int x = CANVAS_WIDTH/2 - BALL_WIDTH/2; //볼의 위치값 x,볼은 처음에 화면중앙에 있음(캔버스 가로/2-공의 반지름)
         int y = CANVAS_HEIGHT/2 - BALL_HEIGHT/2; //볼의 위치값 y
         int width = BALL_WIDTH; //볼의 넓이
         int height =BALL_HEIGHT; //볼의 높이
         
         Point getCenter() {//point라는 x,y를 가지고 있는 클래스가 지정되어 있다. getCenter의 센터값을 x,y에 붙혀주도록 한다. 
            return new Point(x + (BALL_WIDTH/2),y+(BALL_HEIGHT/2));//center값은 ball의 width/2가 된다.
         }
         Point getBottomCenter() {
            return new Point(x + (BALL_WIDTH/2),y+(BALL_HEIGHT));
         }
         Point getTopCenter() {
            return new Point(x + (BALL_WIDTH/2),y);
         }
         Point getLeftCenter() {
            return new Point(x, y+(BALL_HEIGHT/2));
         }
         
         Point getRightCenter() {
            return new Point(x+(BALL_WIDTH/2), y+(BALL_HEIGHT/2));
         }
         
      }
      static class Bar{ //Bar클래스 생성(사용자가 움직아는 정보)
         int x = CANVAS_WIDTH/2 - BAR_WIDTH/2; //바의 위치값 x,바는 처음에 화면중앙에 있음(캔버스 가로/2-바의 반지름)
         int y = CANVAS_HEIGHT-100; //바의 위치값 y, CANVAS_HEIGHT에 100정도 떨어진 지점
         int width = BAR_WIDTH; //위에서 BAR의 WIDTH,HEIGHT값을 상수에서 미리 지정했음
         int height = BAR_HEIGHT;
      }
      static class Block{ //Block이라는 클래스를 만듬
         int x; //블록을 초기화 하지 않는 이유는 블록이 각각 다 다르기 때문임
         int y;
         int width = BLOCK_WIDTH;//위에서 BLOCK의 WIDTH,HEIGHT값을 상수에서 미리 지정했음
         int height = BLOCK_HEIGHT;
         int color =0; //0:white(스코어 10) 1:yellow(20) 2:blue(30) 3:mazanta(40) 4:red(50)
         boolean isHidden = false;//블록을 맞추면 블록이 화면에서 사라지도록 함
      }
      static class MyPanel extends JPanel{//드로잉 역할을 위한 캔버스역할을 하는 클래스
         public MyPanel() {//생성자 MyPanel를 넣어줌
            this.setSize(CANVAS_WIDTH,CANVAS_HEIGHT);//생성자에서는 현재 자신의 사이즈를 지정해주면 됌 현재 자신의 위치는 지정이 됌
            this.setBackground(Color.BLACK);//백그라운드 블랙 ㅋ
         }
         @Override
         public void paint(Graphics g) {
            super.paint(g);//위쪽에 paint 객체를 넘겨줘서 초기화 되었다는 것을 하위클래스에 세팅
            Graphics2D g2d = (Graphics2D)g;//Graphics2D은 그래픽을 그리기 위해 수행해서 지원해주는 그래픽 관련 클래스들
            
            drawUI(g2d);//함수 생성,코드가 길어지면 구조자체가 헷갈림
         }
         private void drawUI(Graphics2D g2d) {//PRIVATE:밖에서 참조 불가능
            //draw Blocks
            for(int i=0; i<BLOCK_ROWS;i++) {//i==블록 행
               for(int j=0; j<BLOCK_COLUMS; j++) {//j=블록 열
                  if(blocks[i][j].isHidden) {//blocks에서 루프가 들어옴 ,is hidden이면 countinue로 루프순환
                     continue;
                  }
                  if(blocks[i][j].color==0) {
                     g2d.setColor(Color.WHITE);//g2d함수를 이용하여 set함수값을 바꿈
                  }
                  else if(blocks[i][j].color==1) {
                     g2d.setColor(Color.YELLOW);
                  }
                  else if(blocks[i][j].color==2) {
                     g2d.setColor(Color.BLUE);
                  }
                  else if(blocks[i][j].color==3) {
                     g2d.setColor(Color.MAGENTA);
                  }
                  else if(blocks[i][j].color==4) {
                     g2d.setColor(Color.RED);
                  }
                  g2d.fillRect(blocks[i][j].x, blocks[i][j].y, //실제로 그리는 부분 블럭의 값을 넣음
                        blocks[i][j].width, blocks[i][j].height);
               }
               //draw score
               g2d.setColor(Color.WHITE);
               g2d.setFont(new Font("TimesRoman",Font.BOLD,20));//폰트종류,굵기,사이즈
               g2d.drawString("Score : "+score,CANVAS_WIDTH/2-200, 20);//문자열 삽입,문자위치
               
               if(isGameFinish) {
                  g2d.setColor(Color.YELLOW);
                  g2d.setFont(new Font("TimesRoman",Font.BOLD,50));//폰트종류,굵기,사이즈
                  g2d.drawString("Score : "+score,CANVAS_WIDTH/2 -130 , 200);//문자열 삽입,문자위치
                  g2d.drawString("Game Finish!",CANVAS_WIDTH/2 -160 , 240);//문자열 삽입,문자위치
               }
               //draw ball
               g2d.setColor(Color.WHITE);
               g2d.fillOval(ball.x, ball.y, BALL_WIDTH, BALL_HEIGHT);//원크기
               
               //draw Bar
               g2d.setColor(Color.WHITE);
               g2d.fillRect(bar.x, bar.y, BAR_WIDTH, BAR_HEIGHT);//바크기
            }            
         }
         
      }
      public MyFrame(String title) {
         super(title); //Jframe에 있는 생성자에 연결
         this.setVisible(true); //true로 해줘야 화면에 보임,this는 Jframe
         this.setSize(CANVAS_WIDTH,CANVAS_HEIGHT); //캔버스 사이즈
         this.setLocation(400,300); //x,y 값을 줌 ui가 보이는 위치
         this.setLayout(new BorderLayout());//BorderLayout 으로 Layout값 지정
         this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//이 옵션을 넣어야 창이 제대로 닫힘
      
         initData(); //init data함수를 호출하여 전체적인 변수데이터를 init(초기화)한다. 
         
         myPanel = new MyPanel();//myPanel을 만들어서 캔버스 역할을 하게 함
         this.add("Center",myPanel);//만든 panel을 전체 프레임에 넣는다.(위치지정)
         
         setKeyListener();//setKeyListener함수를 선언하여 바가 오른쪽,왼쪽으로 움직일 수 있게 한다.
         startTimer();//10미리세크,20미리세크 씩 단위로 타이머 틱이 돌면서 ui를 그리는 함수 생성하여 호출 
      }
      public void initData(){//전체적인 데이터를 초기화 함,10칸짜리 블록을 2중for문을 사용하여 지정
         for(int i=0; i<BLOCK_ROWS;i++) {//i==블록 행
            for(int j=0; j<BLOCK_COLUMS; j++) {//j=블록 열
               blocks[i][j] = new Block();//실제적 객체 위에 행렬 생성은 공간만 만든 것이고 밑의 행렬은 공간 하나에 객체를 생성해 값을 넣음
               blocks[i][j].x = BLOCK_WIDTH*j+BLOCK_GAP*j; //x좌표 각각의 블록마다 위치를 잡아야함 0,40,80으로 진행
               blocks[i][j].y = 100+BLOCK_HEIGHT*i+BLOCK_GAP*i; //y의 위치,상단여백(score점수)을 줌
               blocks[i][j].width = BLOCK_WIDTH;
               blocks[i][j].height = BLOCK_HEIGHT;
               blocks[i][j].color = 4-i;//컬러값 배열로 따지면 맨아래 쪽의 흰색이 (4,0)이므로 반대로 지정해야 색깔이 정확히 찍힘 (0:white(스코어 10) 1:yellow(20) 2:blue(30) 3:magenta(40) 4:red(50))
               blocks[i][j].isHidden = false;//isHidden 의 초기값은 false여야함 나중에 충돌하면 숨겨지도록
            }
         }
      }
       public void setKeyListener(){ //바가 왼쪽,오른쪽으로 움직이게 함
         this.addKeyListener( new KeyAdapter(){ //new keyAdapter는 미리 선언된 객체 익명함수
            @Override
            public void keyPressed(KeyEvent e){ //미리 지정된 keyPressed함수에 keyEvent를 매개변수로 가져옴,키보드를 치면  키이벤트가 e라는 객체변수를 통해 들어옴
               if(e.getKeyCode() == KeyEvent.VK_LEFT){ //어떤키인지 구별하는 방법,왼쪽키가 눌림
                  System.out.println("Pressed Left key");
                   barXTarget -= 20; //바로 값을 넣으면 끊겨서 이동하므로 보관을 위해 만들어 둔 barxTarget변수에 지정
                   if(bar.x < barXTarget) { //실제 bar의 위치 x값이 barXTarget 값보다 작으면 예외처리,현재 값으로 초기화
                      barXTarget = bar.x;
                      
                   }
               }
               else if(e.getKeyCode() == KeyEvent.VK_RIGHT){ //어떤키인지 구별하는 방법,오른쪽키가 눌림
                  System.out.println("Pressed Right key");
                   barXTarget += 20; //바로 값을 넣으면 끊겨서 이동하므로 보관을 위해 만들어 둔 barxTarget변수에 지정
                   if(bar.x > barXTarget) { //실제 bar의 위치 x값이 barXTarget 값보다 크면 예외처리,현재 값(x값)으로 초기화
                      barXTarget = bar.x;
                   }
               }
            }
         });
         
      } 
      public void startTimer(){//타이머에서 값을 그때그때 초기화해서 움직여줌
         timer = new Timer(20,new ActionListener() {//자바가 기본적으로 지원해 주는 것이 아닌 timer = string에서 지원하는 ui적인 타이머,20밀리세컨드 마다 이동
            @Override
            public void actionPerformed(ActionEvent e) {//미리 정의된 함수,타이머 이벤트
                movement();//움직이게 하는 movement라는 함수를 새로 만들어서 움직임을 처리하는 코드블럭을 만듬
                checkCollision();//벽,바 충돌, checkCollision함수를 만들어서 벽과 바에 충돌처리
                checkCollisionBlock();//블록 충돌,벽하고 바에 충돌하는 것과 블록에 충돌하는 것을 따로 함수를 만들어서 50개의 블럭이 처리되도록 함
                myPanel.repaint();//다 끝나고 데이터가 바껴서 url을 새로 업데이트 해야함,myPanel.repaint함수를 호출하여 readraw하도록 함
                
                isGameFinish();
            }
         });
         timer.start();//timer.start를 써야 timer가 start된다.
      }
      public void isGameFinish() {
          int count = 0; 
          for(int i=0; i<BLOCK_ROWS;i++) {
               for(int j=0; j<BLOCK_COLUMS; j++) {
                  Block block = blocks[i][j];
                  if(block.isHidden) //block이 isHidden 이라면 카운터를 올려줌
                     count++;
         
               }
          }
          if(count ==BLOCK_ROWS*BLOCK_COLUMS){//count가 컬럼 갯수와 똑같다면
             //게임 끝
             isGameFinish = true;
             timer.stop();
          }
      }
       public void movement(){//바의 움직임 함수구현
          if(bar.x<barXTarget) {//bar.x가barXTarget(키보드 입력받은 것) 보다 작다면  키워줘야함
             bar.x +=10; 
          }
          else if(bar.x>barXTarget) {//bar.x가 barXTarget(키보드 입력받은 것) 보다 크다면  줄여줘야함
             bar.x -=10;
          }
          if(dir==0) { //ball이 움직이는 방향,0:Up-Right
             ball.x += ballSpeed; //Right은 +
             ball.y -= ballSpeed; //UP은 -
          }
          else if(dir==1) { //1:Down-Right 
             ball.x += ballSpeed; //Right은 +
             ball.y += ballSpeed; //Down은 +
          }
          else if(dir==2) { //2:Up-Left 
             ball.x -= ballSpeed; //Left는 -
             ball.y -= ballSpeed; //UP은 -
             
          }
          else if(dir==3) { //3:Down-Left
             ball.x -= ballSpeed; //Left는 -
             ball.y += ballSpeed; //Down은 +
          }
       }
       public boolean duplRect(Rectangle rect1, Rectangle rect2){ //충돌 했는지 않했는지를 boolean으로 충돌여부 체크
         return rect1.intersects(rect2);//rect에 지원해주는 intersects 함수에  rect2를 입력 , 두개의 사각형 의 영역이 중복되는지 체크한다.
       }
       public void checkCollision(){
          if(dir==0) { //ball이 움직이는 방향,0:Up-Right
         //벽에 충돌 된 경우
             if(ball.y<0) {//윗벽에 부딫혔다.
                dir = 1; //디렉션 값이 1
                
             }
             if(ball.x>CANVAS_WIDTH-BALL_WIDTH-BALL_WIDTH) { //오른쪽 벽에 부딫침
                dir = 2; //디렉션 값이 2
             }
             //위로 갈때 Bar에 부딫힐 일이 없으므로 none,내려갈때만 바에 부딫힘
          }
          else if(dir==1) { //1:Down-Right 
             if(ball.y>CANVAS_HEIGHT-BALL_HEIGHT-BALL_HEIGHT-BALL_HEIGHT) {//아랫쪽벽에 부딫혔다.
                dir = 0; //디렉션 값이 0
                
                dir = 0; //초기화
                ball.x = CANVAS_WIDTH/2 - BALL_WIDTH/2; //볼의 위치값을 처음 세팅값으로 지정
                ball.y = CANVAS_HEIGHT/2 - BALL_HEIGHT/2; 
                score = 0;
             }
             if(ball.x>CANVAS_WIDTH-BALL_WIDTH-BALL_WIDTH) { //오른쪽 벽에 부딫침
                dir = 3; //디렉션 값이 3
             }
            //Bar의 충돌 여부확인
             if(ball.getBottomCenter().y>= bar.y){//아래로 내려가기 때문에 bottom center값을 확인, 정확하게 사각형과 충돌했을지는 알 수 없다.         
                if( duplRect(new Rectangle (ball.x,ball.y,ball.width, ball.height), //true면 충돌됐음,Rectangle라는 클래스를 주고 x, y, width,Height값을 줌
                          new Rectangle(bar.x,bar.y,bar.width, bar.height ))){
                   dir = 0;//밑에 벽에 부딫힘
                }
             }
          
          }
          else if(dir==2) { //2:Up-Left 
             if(ball.y<0) {//윗벽에 부딫혔다.
                dir = 3; //디렉션 값이 3
             }
             if(ball.x <0) {//왼벽에 부딫혔다.
                dir = 0; //디렉션 값이 0
             }
             //위로 갈때 Bar에 부딫힐 일이 없으므로 none,내려갈때만 바에 부딫힘
             
          }
          else if(dir==3) { //3:Down-Left
             if(ball.y>CANVAS_HEIGHT-BALL_HEIGHT-BALL_HEIGHT-BALL_HEIGHT) {//아랫쪽벽에 부딫혔다.
                dir = 2; //디렉션 값이 2
                
                //아랫벽에 부딫혔을 때 게임 reset
                dir = 0; //초기화
                ball.x = CANVAS_WIDTH/2 - BALL_WIDTH/2; //볼의 위치값을 처음 세팅값으로 지정
                ball.y = CANVAS_HEIGHT/2 - BALL_HEIGHT/2; 
                score = 0;
             }
             if(ball.x <0) {//왼벽에 부딫혔다.
                dir = 1; //디렉션 값이 1
             }
               //Bar의 충돌 여부확인
             if(ball.getBottomCenter().y>= bar.y){//아래로 내려가기 때문에 bottom center값을 확인, 정확하게 사각형과 충돌했을지는 알 수 없다.         
                if( duplRect(new Rectangle (ball.x,ball.y,ball.width, ball.height), //true면 충돌됐음,Rectangle라는 클래스를 주고 x, y, width,Height값을 줌
                          new Rectangle(bar.x,bar.y,bar.width, bar.height ))){
                   dir = 2;//밑에 벽에 부딫힘
                }
             }
             
          } 
       }
       public void checkCollisionBlock(){//블록에 대한 충돌처리 
          //0:Up-Right 1:Down-Right 2:Up-Left 3:Down-Left
         //j=블록 열//2중 for문으로 50개의 블록 처리
          for(int i=0; i<BLOCK_ROWS;i++) {//i==블록 행
               for(int j=0; j<BLOCK_COLUMS; j++) {
                  Block block = blocks[i][j];//해당되는 block을 하나 가져옴
                  //이 블록의 충돌여부 확인
                  if(block.isHidden ==false ){//블록이 충돌되지 않았다면
                     if(dir == 0) { //0:Up-Right
                        if( duplRect(new Rectangle (ball.x,ball.y,ball.width, ball.height), //true면 충돌됐음,Rectangle라는 클래스를 주고 x, y, width,Height값을 줌
                                   new Rectangle(block.x,block.y,block.width, block.height ))){
                           if(ball.x>block.x+2 && //2라는 gap의 영향을 줘서 안쪽으로 들어갔을 때 체크되도록 한다.
                                 ball.getRightCenter().x <= block.x + block.width -2){ 
                              //블록의 아랫쪽에 부딫힌 경우
                              dir = 1;
                           }
                           else {//블록의 옆벽에 부딫친 경우
                              dir = 2;
                           }
                           block.isHidden = true; //다음에 충돌이 안되도록 한다.
                           //score점수
                           if(block.color==0) { 
                              score += 10; //블록이 white면 score를 10점 올린다.
                           }
                           else if(block.color==1) { 
                              score += 20; //블록이 yellow면 score를 20점 올린다.
                           }
                           else if(block.color==2) { 
                              score += 30; //블록이 blue면 score를 30점 올린다.
                           }
                           else if(block.color==3) { 
                              score += 40; //블록이 magenta면 score를 40점 올린다.
                           }
                           else if(block.color==3) { 
                              score += 40; //블록이 red면 score를 50점 올린다.
                           }
                           
                        }
                     }
                     else if(dir == 1) { //1:Down-Right
                        if( duplRect(new Rectangle (ball.x,ball.y,ball.width, ball.height), //true면 충돌됐음,Rectangle라는 클래스를 주고 x, y, width,Height값을 줌
                                new Rectangle(block.x,block.y,block.width, block.height ))){
                           if(ball.x>block.x+2 && //2라는 gap의 영향을 줘서 안쪽으로 들어갔을 때 체크되도록 한다.
                                 ball.getRightCenter().x <= block.x + block.width -2){ 
                              //블록의 윗벽에 부딫힌 경우
                              dir = 0;
                           }
                           else {//블록의 옆벽에 부딫친 경우
                              dir = 3;
                           }
                           block.isHidden = true; //다음에 충돌이 안되도록 한다.
                           //score점수
                           if(block.color==0) { 
                              score += 10; //블록이 white면 score를 10점 올린다.
                           }
                           else if(block.color==1) { 
                              score += 20; //블록이 yellow면 score를 20점 올린다.
                           }
                           else if(block.color==2) { 
                              score += 30; //블록이 blue면 score를 30점 올린다.
                           }
                           else if(block.color==3) { 
                              score += 40; //블록이 magenta면 score를 40점 올린다.
                           }
                           else if(block.color==3) { 
                              score += 40; //블록이 red면 score를 50점 올린다.
                           }
                     }
                     }
                     else if(dir == 2) { //2:Up-Left
                        if( duplRect(new Rectangle (ball.x,ball.y,ball.width, ball.height), //true면 충돌됐음,Rectangle라는 클래스를 주고 x, y, width,Height값을 줌
                                new Rectangle(block.x,block.y,block.width, block.height ))){
                           if(ball.x>block.x+2 && //2라는 gap의 영향을 줘서 안쪽으로 들어갔을 때 체크되도록 한다.
                                 ball.getRightCenter().x <= block.x + block.width -2){ 
                              //블록의 바닥벽에 부딫힌 경우
                              dir = 3;
                           }
                           else {//블록의 오른쪽 벽에 부딫친 경우
                              dir = 0;
                           }
                           block.isHidden = true; //다음에 충돌이 안되도록 한다.
                           //score점수
                           if(block.color==0) { 
                              score += 10; //블록이 white면 score를 10점 올린다.
                           }
                           else if(block.color==1) { 
                              score += 20; //블록이 yellow면 score를 20점 올린다.
                           }
                           else if(block.color==2) { 
                              score += 30; //블록이 blue면 score를 30점 올린다.
                           }
                           else if(block.color==3) { 
                              score += 40; //블록이 magenta면 score를 40점 올린다.
                           }
                           else if(block.color==3) { 
                              score += 40; //블록이 red면 score를 50점 올린다.
                           }
                        }
                           
                     }
                     else if(dir==3) { //3:Down-Left
                        if( duplRect(new Rectangle (ball.x,ball.y,ball.width, ball.height), //true면 충돌됐음,Rectangle라는 클래스를 주고 x, y, width,Height값을 줌
                                new Rectangle(block.x,block.y,block.width, block.height ))){
                           if(ball.x>block.x+2 && //2라는 gap의 영향을 줘서 안쪽으로 들어갔을 때 체크되도록 한다.
                              ball.getRightCenter().x <= block.x + block.width -2){ 
                              //블록의 윗벽에 부딫힌 경우
                              dir = 2;
                           }
                           else {//블록의 오른쪽벽에 부딫친 경우
                              dir = 1;
                           }
                           block.isHidden = true; //다음에 충돌이 안되도록 한다.
                           //score점수
                           if(block.color==0) { 
                              score += 10; //블록이 white면 score를 10점 올린다.
                           }
                           else if(block.color==1) { 
                              score += 20; //블록이 yellow면 score를 20점 올린다.
                           }
                           else if(block.color==2) { 
                              score += 30; //블록이 blue면 score를 30점 올린다.
                           }
                           else if(block.color==3) { 
                              score += 40; //블록이 magenta면 score를 40점 올린다.
                           }
                           else if(block.color==3) { 
                              score += 40; //블록이 red면 score를 50점 올린다.
                           }
                     }
                  }
               }
            }
         }
      }
   }
   public static void main(String[] args) {
      // TODO Auto-generated method stub
      new MyFrame("Block Game");
   }

}
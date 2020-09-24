import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class blockgame {
   
   static class MyFrame extends JFrame{ //Jframe�� ���
      //constant �����ȣ�� ����
      static int BALL_WIDTH = 20; //������
      static int BALL_HEIGHT = 20; //�� ����
      static int BLOCK_ROWS = 5; //����� ��(��) ��
      static int BLOCK_COLUMS = 10; //����� �÷�(��) ��
      static int BLOCK_WIDTH = 40; //�ϳ��� ����� ����
      static int BLOCK_HEIGHT = 20; //����� ����
      static int BLOCK_GAP = 3; //��ϰ� ��ϻ����� ����
      static int BAR_WIDTH = 80; //����ڰ� ������ ���� ����
      static int BAR_HEIGHT = 20; //����ڰ� ������ ���� ����
      static int CANVAS_WIDTH = 400+(BLOCK_GAP*BLOCK_COLUMS)-BLOCK_GAP; //ķ������ ����(BLOCK_WITHS*BLOCK_COLUMS=400+(BLOCK_GAP*(�÷��� ������ŭ)-�ǿ����� ��)
      static int CANVAS_HEIGHT = 600;
      
      //variable ��������
      static MyPanel myPanel = null;//�г�(�׸���)�� ����� ��ü����
      static int score = 0; //������ ǥ���ϴ� ���� ����
      static Timer timer = null; //
      static Block[][] blocks = new Block[BLOCK_ROWS][BLOCK_COLUMS];//block�̶�� Ŭ������ ����� 2���迭�� ����   
      static Bar bar = new Bar(); //���� ������ ������ �ִ� Ŭ���� ��ü
      static Ball ball = new Ball(); //���� ������ ���� Ŭ���� ��ü
      static int barXTarget = bar.x; //barXTarget�̶�� ������ ����,bar�� x��� ������ ������ �ִ�.(Target Value-interpolation(�ѹ��� ���� ���� ����))
      static int dir = 0; //���� �����̴� ���� 4����  0:Up-Right 1:Down-Right 2:Up-Left 3:Down-Left
      static int ballSpeed = 5; //���� �ӵ�
      static boolean isGameFinish = false;  
      
      static class Ball{ //Ball������ ���� Ŭ���� ���� 
         int x = CANVAS_WIDTH/2 - BALL_WIDTH/2; //���� ��ġ�� x,���� ó���� ȭ���߾ӿ� ����(ĵ���� ����/2-���� ������)
         int y = CANVAS_HEIGHT/2 - BALL_HEIGHT/2; //���� ��ġ�� y
         int width = BALL_WIDTH; //���� ����
         int height =BALL_HEIGHT; //���� ����
         
         Point getCenter() {//point��� x,y�� ������ �ִ� Ŭ������ �����Ǿ� �ִ�. getCenter�� ���Ͱ��� x,y�� �����ֵ��� �Ѵ�. 
            return new Point(x + (BALL_WIDTH/2),y+(BALL_HEIGHT/2));//center���� ball�� width/2�� �ȴ�.
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
      static class Bar{ //BarŬ���� ����(����ڰ� �����ƴ� ����)
         int x = CANVAS_WIDTH/2 - BAR_WIDTH/2; //���� ��ġ�� x,�ٴ� ó���� ȭ���߾ӿ� ����(ĵ���� ����/2-���� ������)
         int y = CANVAS_HEIGHT-100; //���� ��ġ�� y, CANVAS_HEIGHT�� 100���� ������ ����
         int width = BAR_WIDTH; //������ BAR�� WIDTH,HEIGHT���� ������� �̸� ��������
         int height = BAR_HEIGHT;
      }
      static class Block{ //Block�̶�� Ŭ������ ����
         int x; //����� �ʱ�ȭ ���� �ʴ� ������ ����� ���� �� �ٸ��� ������
         int y;
         int width = BLOCK_WIDTH;//������ BLOCK�� WIDTH,HEIGHT���� ������� �̸� ��������
         int height = BLOCK_HEIGHT;
         int color =0; //0:white(���ھ� 10) 1:yellow(20) 2:blue(30) 3:mazanta(40) 4:red(50)
         boolean isHidden = false;//����� ���߸� ����� ȭ�鿡�� ��������� ��
      }
      static class MyPanel extends JPanel{//����� ������ ���� ĵ���������� �ϴ� Ŭ����
         public MyPanel() {//������ MyPanel�� �־���
            this.setSize(CANVAS_WIDTH,CANVAS_HEIGHT);//�����ڿ����� ���� �ڽ��� ����� �������ָ� �� ���� �ڽ��� ��ġ�� ������ ��
            this.setBackground(Color.BLACK);//��׶��� �� ��
         }
         @Override
         public void paint(Graphics g) {
            super.paint(g);//���ʿ� paint ��ü�� �Ѱ��༭ �ʱ�ȭ �Ǿ��ٴ� ���� ����Ŭ������ ����
            Graphics2D g2d = (Graphics2D)g;//Graphics2D�� �׷����� �׸��� ���� �����ؼ� �������ִ� �׷��� ���� Ŭ������
            
            drawUI(g2d);//�Լ� ����,�ڵ尡 ������� ������ü�� �򰥸�
         }
         private void drawUI(Graphics2D g2d) {//PRIVATE:�ۿ��� ���� �Ұ���
            //draw Blocks
            for(int i=0; i<BLOCK_ROWS;i++) {//i==��� ��
               for(int j=0; j<BLOCK_COLUMS; j++) {//j=��� ��
                  if(blocks[i][j].isHidden) {//blocks���� ������ ���� ,is hidden�̸� countinue�� ������ȯ
                     continue;
                  }
                  if(blocks[i][j].color==0) {
                     g2d.setColor(Color.WHITE);//g2d�Լ��� �̿��Ͽ� set�Լ����� �ٲ�
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
                  g2d.fillRect(blocks[i][j].x, blocks[i][j].y, //������ �׸��� �κ� ���� ���� ����
                        blocks[i][j].width, blocks[i][j].height);
               }
               //draw score
               g2d.setColor(Color.WHITE);
               g2d.setFont(new Font("TimesRoman",Font.BOLD,20));//��Ʈ����,����,������
               g2d.drawString("Score : "+score,CANVAS_WIDTH/2-200, 20);//���ڿ� ����,������ġ
               
               if(isGameFinish) {
                  g2d.setColor(Color.YELLOW);
                  g2d.setFont(new Font("TimesRoman",Font.BOLD,50));//��Ʈ����,����,������
                  g2d.drawString("Score : "+score,CANVAS_WIDTH/2 -130 , 200);//���ڿ� ����,������ġ
                  g2d.drawString("Game Finish!",CANVAS_WIDTH/2 -160 , 240);//���ڿ� ����,������ġ
               }
               //draw ball
               g2d.setColor(Color.WHITE);
               g2d.fillOval(ball.x, ball.y, BALL_WIDTH, BALL_HEIGHT);//��ũ��
               
               //draw Bar
               g2d.setColor(Color.WHITE);
               g2d.fillRect(bar.x, bar.y, BAR_WIDTH, BAR_HEIGHT);//��ũ��
            }            
         }
         
      }
      public MyFrame(String title) {
         super(title); //Jframe�� �ִ� �����ڿ� ����
         this.setVisible(true); //true�� ����� ȭ�鿡 ����,this�� Jframe
         this.setSize(CANVAS_WIDTH,CANVAS_HEIGHT); //ĵ���� ������
         this.setLocation(400,300); //x,y ���� �� ui�� ���̴� ��ġ
         this.setLayout(new BorderLayout());//BorderLayout ���� Layout�� ����
         this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//�� �ɼ��� �־�� â�� ����� ����
      
         initData(); //init data�Լ��� ȣ���Ͽ� ��ü���� ���������͸� init(�ʱ�ȭ)�Ѵ�. 
         
         myPanel = new MyPanel();//myPanel�� ���� ĵ���� ������ �ϰ� ��
         this.add("Center",myPanel);//���� panel�� ��ü �����ӿ� �ִ´�.(��ġ����)
         
         setKeyListener();//setKeyListener�Լ��� �����Ͽ� �ٰ� ������,�������� ������ �� �ְ� �Ѵ�.
         startTimer();//10�̸���ũ,20�̸���ũ �� ������ Ÿ�̸� ƽ�� ���鼭 ui�� �׸��� �Լ� �����Ͽ� ȣ�� 
      }
      public void initData(){//��ü���� �����͸� �ʱ�ȭ ��,10ĭ¥�� ����� 2��for���� ����Ͽ� ����
         for(int i=0; i<BLOCK_ROWS;i++) {//i==��� ��
            for(int j=0; j<BLOCK_COLUMS; j++) {//j=��� ��
               blocks[i][j] = new Block();//������ ��ü ���� ��� ������ ������ ���� ���̰� ���� ����� ���� �ϳ��� ��ü�� ������ ���� ����
               blocks[i][j].x = BLOCK_WIDTH*j+BLOCK_GAP*j; //x��ǥ ������ ��ϸ��� ��ġ�� ��ƾ��� 0,40,80���� ����
               blocks[i][j].y = 100+BLOCK_HEIGHT*i+BLOCK_GAP*i; //y�� ��ġ,��ܿ���(score����)�� ��
               blocks[i][j].width = BLOCK_WIDTH;
               blocks[i][j].height = BLOCK_HEIGHT;
               blocks[i][j].color = 4-i;//�÷��� �迭�� ������ �ǾƷ� ���� ����� (4,0)�̹Ƿ� �ݴ�� �����ؾ� ������ ��Ȯ�� ���� (0:white(���ھ� 10) 1:yellow(20) 2:blue(30) 3:magenta(40) 4:red(50))
               blocks[i][j].isHidden = false;//isHidden �� �ʱⰪ�� false������ ���߿� �浹�ϸ� ����������
            }
         }
      }
       public void setKeyListener(){ //�ٰ� ����,���������� �����̰� ��
         this.addKeyListener( new KeyAdapter(){ //new keyAdapter�� �̸� ����� ��ü �͸��Լ�
            @Override
            public void keyPressed(KeyEvent e){ //�̸� ������ keyPressed�Լ��� keyEvent�� �Ű������� ������,Ű���带 ġ��  Ű�̺�Ʈ�� e��� ��ü������ ���� ����
               if(e.getKeyCode() == KeyEvent.VK_LEFT){ //�Ű���� �����ϴ� ���,����Ű�� ����
                  System.out.println("Pressed Left key");
                   barXTarget -= 20; //�ٷ� ���� ������ ���ܼ� �̵��ϹǷ� ������ ���� ����� �� barxTarget������ ����
                   if(bar.x < barXTarget) { //���� bar�� ��ġ x���� barXTarget ������ ������ ����ó��,���� ������ �ʱ�ȭ
                      barXTarget = bar.x;
                      
                   }
               }
               else if(e.getKeyCode() == KeyEvent.VK_RIGHT){ //�Ű���� �����ϴ� ���,������Ű�� ����
                  System.out.println("Pressed Right key");
                   barXTarget += 20; //�ٷ� ���� ������ ���ܼ� �̵��ϹǷ� ������ ���� ����� �� barxTarget������ ����
                   if(bar.x > barXTarget) { //���� bar�� ��ġ x���� barXTarget ������ ũ�� ����ó��,���� ��(x��)���� �ʱ�ȭ
                      barXTarget = bar.x;
                   }
               }
            }
         });
         
      } 
      public void startTimer(){//Ÿ�̸ӿ��� ���� �׶��׶� �ʱ�ȭ�ؼ� ��������
         timer = new Timer(20,new ActionListener() {//�ڹٰ� �⺻������ ������ �ִ� ���� �ƴ� timer = string���� �����ϴ� ui���� Ÿ�̸�,20�и������� ���� �̵�
            @Override
            public void actionPerformed(ActionEvent e) {//�̸� ���ǵ� �Լ�,Ÿ�̸� �̺�Ʈ
                movement();//�����̰� �ϴ� movement��� �Լ��� ���� ���� �������� ó���ϴ� �ڵ���� ����
                checkCollision();//��,�� �浹, checkCollision�Լ��� ���� ���� �ٿ� �浹ó��
                checkCollisionBlock();//��� �浹,���ϰ� �ٿ� �浹�ϴ� �Ͱ� ��Ͽ� �浹�ϴ� ���� ���� �Լ��� ���� 50���� ���� ó���ǵ��� ��
                myPanel.repaint();//�� ������ �����Ͱ� �ٲ��� url�� ���� ������Ʈ �ؾ���,myPanel.repaint�Լ��� ȣ���Ͽ� readraw�ϵ��� ��
                
                isGameFinish();
            }
         });
         timer.start();//timer.start�� ��� timer�� start�ȴ�.
      }
      public void isGameFinish() {
          int count = 0; 
          for(int i=0; i<BLOCK_ROWS;i++) {
               for(int j=0; j<BLOCK_COLUMS; j++) {
                  Block block = blocks[i][j];
                  if(block.isHidden) //block�� isHidden �̶�� ī���͸� �÷���
                     count++;
         
               }
          }
          if(count ==BLOCK_ROWS*BLOCK_COLUMS){//count�� �÷� ������ �Ȱ��ٸ�
             //���� ��
             isGameFinish = true;
             timer.stop();
          }
      }
       public void movement(){//���� ������ �Լ�����
          if(bar.x<barXTarget) {//bar.x��barXTarget(Ű���� �Է¹��� ��) ���� �۴ٸ�  Ű�������
             bar.x +=10; 
          }
          else if(bar.x>barXTarget) {//bar.x�� barXTarget(Ű���� �Է¹��� ��) ���� ũ�ٸ�  �ٿ������
             bar.x -=10;
          }
          if(dir==0) { //ball�� �����̴� ����,0:Up-Right
             ball.x += ballSpeed; //Right�� +
             ball.y -= ballSpeed; //UP�� -
          }
          else if(dir==1) { //1:Down-Right 
             ball.x += ballSpeed; //Right�� +
             ball.y += ballSpeed; //Down�� +
          }
          else if(dir==2) { //2:Up-Left 
             ball.x -= ballSpeed; //Left�� -
             ball.y -= ballSpeed; //UP�� -
             
          }
          else if(dir==3) { //3:Down-Left
             ball.x -= ballSpeed; //Left�� -
             ball.y += ballSpeed; //Down�� +
          }
       }
       public boolean duplRect(Rectangle rect1, Rectangle rect2){ //�浹 �ߴ��� ���ߴ����� boolean���� �浹���� üũ
         return rect1.intersects(rect2);//rect�� �������ִ� intersects �Լ���  rect2�� �Է� , �ΰ��� �簢�� �� ������ �ߺ��Ǵ��� üũ�Ѵ�.
       }
       public void checkCollision(){
          if(dir==0) { //ball�� �����̴� ����,0:Up-Right
         //���� �浹 �� ���
             if(ball.y<0) {//������ �΋H����.
                dir = 1; //�𷺼� ���� 1
                
             }
             if(ball.x>CANVAS_WIDTH-BALL_WIDTH-BALL_WIDTH) { //������ ���� �΋Hħ
                dir = 2; //�𷺼� ���� 2
             }
             //���� ���� Bar�� �΋H�� ���� �����Ƿ� none,���������� �ٿ� �΋H��
          }
          else if(dir==1) { //1:Down-Right 
             if(ball.y>CANVAS_HEIGHT-BALL_HEIGHT-BALL_HEIGHT-BALL_HEIGHT) {//�Ʒ��ʺ��� �΋H����.
                dir = 0; //�𷺼� ���� 0
                
                dir = 0; //�ʱ�ȭ
                ball.x = CANVAS_WIDTH/2 - BALL_WIDTH/2; //���� ��ġ���� ó�� ���ð����� ����
                ball.y = CANVAS_HEIGHT/2 - BALL_HEIGHT/2; 
                score = 0;
             }
             if(ball.x>CANVAS_WIDTH-BALL_WIDTH-BALL_WIDTH) { //������ ���� �΋Hħ
                dir = 3; //�𷺼� ���� 3
             }
            //Bar�� �浹 ����Ȯ��
             if(ball.getBottomCenter().y>= bar.y){//�Ʒ��� �������� ������ bottom center���� Ȯ��, ��Ȯ�ϰ� �簢���� �浹�������� �� �� ����.         
                if( duplRect(new Rectangle (ball.x,ball.y,ball.width, ball.height), //true�� �浹����,Rectangle��� Ŭ������ �ְ� x, y, width,Height���� ��
                          new Rectangle(bar.x,bar.y,bar.width, bar.height ))){
                   dir = 0;//�ؿ� ���� �΋H��
                }
             }
          
          }
          else if(dir==2) { //2:Up-Left 
             if(ball.y<0) {//������ �΋H����.
                dir = 3; //�𷺼� ���� 3
             }
             if(ball.x <0) {//�޺��� �΋H����.
                dir = 0; //�𷺼� ���� 0
             }
             //���� ���� Bar�� �΋H�� ���� �����Ƿ� none,���������� �ٿ� �΋H��
             
          }
          else if(dir==3) { //3:Down-Left
             if(ball.y>CANVAS_HEIGHT-BALL_HEIGHT-BALL_HEIGHT-BALL_HEIGHT) {//�Ʒ��ʺ��� �΋H����.
                dir = 2; //�𷺼� ���� 2
                
                //�Ʒ����� �΋H���� �� ���� reset
                dir = 0; //�ʱ�ȭ
                ball.x = CANVAS_WIDTH/2 - BALL_WIDTH/2; //���� ��ġ���� ó�� ���ð����� ����
                ball.y = CANVAS_HEIGHT/2 - BALL_HEIGHT/2; 
                score = 0;
             }
             if(ball.x <0) {//�޺��� �΋H����.
                dir = 1; //�𷺼� ���� 1
             }
               //Bar�� �浹 ����Ȯ��
             if(ball.getBottomCenter().y>= bar.y){//�Ʒ��� �������� ������ bottom center���� Ȯ��, ��Ȯ�ϰ� �簢���� �浹�������� �� �� ����.         
                if( duplRect(new Rectangle (ball.x,ball.y,ball.width, ball.height), //true�� �浹����,Rectangle��� Ŭ������ �ְ� x, y, width,Height���� ��
                          new Rectangle(bar.x,bar.y,bar.width, bar.height ))){
                   dir = 2;//�ؿ� ���� �΋H��
                }
             }
             
          } 
       }
       public void checkCollisionBlock(){//��Ͽ� ���� �浹ó�� 
          //0:Up-Right 1:Down-Right 2:Up-Left 3:Down-Left
         //j=��� ��//2�� for������ 50���� ��� ó��
          for(int i=0; i<BLOCK_ROWS;i++) {//i==��� ��
               for(int j=0; j<BLOCK_COLUMS; j++) {
                  Block block = blocks[i][j];//�ش�Ǵ� block�� �ϳ� ������
                  //�� ����� �浹���� Ȯ��
                  if(block.isHidden ==false ){//����� �浹���� �ʾҴٸ�
                     if(dir == 0) { //0:Up-Right
                        if( duplRect(new Rectangle (ball.x,ball.y,ball.width, ball.height), //true�� �浹����,Rectangle��� Ŭ������ �ְ� x, y, width,Height���� ��
                                   new Rectangle(block.x,block.y,block.width, block.height ))){
                           if(ball.x>block.x+2 && //2��� gap�� ������ �༭ �������� ���� �� üũ�ǵ��� �Ѵ�.
                                 ball.getRightCenter().x <= block.x + block.width -2){ 
                              //����� �Ʒ��ʿ� �΋H�� ���
                              dir = 1;
                           }
                           else {//����� ������ �΋Hģ ���
                              dir = 2;
                           }
                           block.isHidden = true; //������ �浹�� �ȵǵ��� �Ѵ�.
                           //score����
                           if(block.color==0) { 
                              score += 10; //����� white�� score�� 10�� �ø���.
                           }
                           else if(block.color==1) { 
                              score += 20; //����� yellow�� score�� 20�� �ø���.
                           }
                           else if(block.color==2) { 
                              score += 30; //����� blue�� score�� 30�� �ø���.
                           }
                           else if(block.color==3) { 
                              score += 40; //����� magenta�� score�� 40�� �ø���.
                           }
                           else if(block.color==3) { 
                              score += 40; //����� red�� score�� 50�� �ø���.
                           }
                           
                        }
                     }
                     else if(dir == 1) { //1:Down-Right
                        if( duplRect(new Rectangle (ball.x,ball.y,ball.width, ball.height), //true�� �浹����,Rectangle��� Ŭ������ �ְ� x, y, width,Height���� ��
                                new Rectangle(block.x,block.y,block.width, block.height ))){
                           if(ball.x>block.x+2 && //2��� gap�� ������ �༭ �������� ���� �� üũ�ǵ��� �Ѵ�.
                                 ball.getRightCenter().x <= block.x + block.width -2){ 
                              //����� ������ �΋H�� ���
                              dir = 0;
                           }
                           else {//����� ������ �΋Hģ ���
                              dir = 3;
                           }
                           block.isHidden = true; //������ �浹�� �ȵǵ��� �Ѵ�.
                           //score����
                           if(block.color==0) { 
                              score += 10; //����� white�� score�� 10�� �ø���.
                           }
                           else if(block.color==1) { 
                              score += 20; //����� yellow�� score�� 20�� �ø���.
                           }
                           else if(block.color==2) { 
                              score += 30; //����� blue�� score�� 30�� �ø���.
                           }
                           else if(block.color==3) { 
                              score += 40; //����� magenta�� score�� 40�� �ø���.
                           }
                           else if(block.color==3) { 
                              score += 40; //����� red�� score�� 50�� �ø���.
                           }
                     }
                     }
                     else if(dir == 2) { //2:Up-Left
                        if( duplRect(new Rectangle (ball.x,ball.y,ball.width, ball.height), //true�� �浹����,Rectangle��� Ŭ������ �ְ� x, y, width,Height���� ��
                                new Rectangle(block.x,block.y,block.width, block.height ))){
                           if(ball.x>block.x+2 && //2��� gap�� ������ �༭ �������� ���� �� üũ�ǵ��� �Ѵ�.
                                 ball.getRightCenter().x <= block.x + block.width -2){ 
                              //����� �ٴں��� �΋H�� ���
                              dir = 3;
                           }
                           else {//����� ������ ���� �΋Hģ ���
                              dir = 0;
                           }
                           block.isHidden = true; //������ �浹�� �ȵǵ��� �Ѵ�.
                           //score����
                           if(block.color==0) { 
                              score += 10; //����� white�� score�� 10�� �ø���.
                           }
                           else if(block.color==1) { 
                              score += 20; //����� yellow�� score�� 20�� �ø���.
                           }
                           else if(block.color==2) { 
                              score += 30; //����� blue�� score�� 30�� �ø���.
                           }
                           else if(block.color==3) { 
                              score += 40; //����� magenta�� score�� 40�� �ø���.
                           }
                           else if(block.color==3) { 
                              score += 40; //����� red�� score�� 50�� �ø���.
                           }
                        }
                           
                     }
                     else if(dir==3) { //3:Down-Left
                        if( duplRect(new Rectangle (ball.x,ball.y,ball.width, ball.height), //true�� �浹����,Rectangle��� Ŭ������ �ְ� x, y, width,Height���� ��
                                new Rectangle(block.x,block.y,block.width, block.height ))){
                           if(ball.x>block.x+2 && //2��� gap�� ������ �༭ �������� ���� �� üũ�ǵ��� �Ѵ�.
                              ball.getRightCenter().x <= block.x + block.width -2){ 
                              //����� ������ �΋H�� ���
                              dir = 2;
                           }
                           else {//����� �����ʺ��� �΋Hģ ���
                              dir = 1;
                           }
                           block.isHidden = true; //������ �浹�� �ȵǵ��� �Ѵ�.
                           //score����
                           if(block.color==0) { 
                              score += 10; //����� white�� score�� 10�� �ø���.
                           }
                           else if(block.color==1) { 
                              score += 20; //����� yellow�� score�� 20�� �ø���.
                           }
                           else if(block.color==2) { 
                              score += 30; //����� blue�� score�� 30�� �ø���.
                           }
                           else if(block.color==3) { 
                              score += 40; //����� magenta�� score�� 40�� �ø���.
                           }
                           else if(block.color==3) { 
                              score += 40; //����� red�� score�� 50�� �ø���.
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
package com.agpfd.whackamole;


import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
public class WhackAMoleView extends SurfaceView implements SurfaceHolder.Callback {
    
    private Context myContext;
    private SurfaceHolder mySurfaceHolder;
    private Bitmap backgroundImg;
    private Bitmap intro;
    private Bitmap Menu;
   
    private boolean showSettings;
    private Bitmap Settings;
    private Bitmap SettingMenu;
    private Bitmap GameOver;
    
    private Bitmap Play;
    private int PlayX;
    private int PlayY;
    private boolean canMove;
    private int showAnimationMenu;
       
    private Bitmap Arrow;
    
    
    
    
    private Bitmap rabit;
    private int Rabit_Width ;
    private int Rabit_Height;
    private int Rabit_Rows = 2;
    private int Rabit_Cols = 3;
    private int YMaskValue =0;
    
    private int screenW = 1;
    private int screenH = 1;
    private boolean running;
    private boolean onTitle;
    private boolean onMenu;
    private boolean onIntro;
    private boolean ActiveRabit;
    private boolean DrawScore;
   
    //Fonts attribute
    private Typeface font;
    private Paint backPaint;
    

    private GameThread thread;
    
    //My Game attributes Variable Declaration
    private int backgroundOrigW;
    private int backgroundOrigH;
    private float ScaleWidth;
    private float ScaleHeight;
    private float drawScaleWidth=0;
    private float drawScaleHeight=0;
    private Bitmap FrontMask;
    private Bitmap Scores;
    private int ScoreXDirection;
    private int ScoreYDirection;

    
    //My Character Position on Screen so it can render according to our image
    private int [] CharacterPosX = new int[7]; 
    private int [] CharacterPosY = new int[7]; 
    private int [] CharacterMasPosX = new int[7];
    private int [] CharacterMasPosY = new int[7];
    private Bitmap  [] MainCharacter = new Bitmap[5];


    
    
    //My Game Character Motions
    private int ActiveCharacter = 0;   //if character is in motion
    private boolean CharacterIsRaising = true; //character is going up
    private boolean CharacterIsFalling = false; //character is falling
    private int CharacterRate = 5;  //this is the variable that determine the height of our character 
    private boolean CharacterHit = false;  //the variable that check if character has  been hit 
    
    //Detecting if character is hit and count it as well as if is miss
    private boolean [] isSquized = new boolean[7]; // check if it is squized 
    private int CharacterSquized = 0; //counting character hits 
    private int CharacterIsMissed = 0; //counting character missed
    private  int Score = 0 ; //our score variable
    private int animatedScore = 0;

    
    private Bitmap [] SuperPower= new Bitmap[5];
    private boolean [] SuperPowerGain = new boolean[5];
    private int [] SuperPowerCount = new int[5];
    private int [] DrawablePower = new int[5];
    
    

    //Sound Settings
    private static SoundPool sounds;
   // private static MediaPlayer music;
    private static int whackSound;
    private static int missSound;
    private static int Here;
    private static int myHeadHurts;
    public boolean [] soundOn  = new boolean[7];
    public boolean musicOn = true;
    
    
    
    
    
    //Delay option 
    private static  final ScheduledExecutorService booleanAtrs =  Executors.newSingleThreadScheduledExecutor();

    
    //detect finger position when screen is touch 
    private int FingerX;
    private int FingerY;
	
	public WhackAMoleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        SurfaceHolder holder = getHolder(); 
        holder.addCallback(this);

        onMenu = false;
        onIntro = true;
        DrawScore = false;
        onTitle = false;
        running = false;
        showSettings = false;
        
        sounds = new SoundPool(2,
        		AudioManager.STREAM_MUSIC, 0);
        whackSound = sounds.load(context, R.raw.ouch, 1); 
        missSound = sounds.load(context, R.raw.miss2, 1);
        Here = sounds.load(context, R.raw.here,1);
        myHeadHurts = sounds.load(context, R.raw.myheadhurts,1);
        
        
        
        
        
        
        for(int i=0; i<CharacterPosX.length; i++){CharacterPosX[i]=0; CharacterPosY[i]=0;}
        for(int i=0; i<SuperPowerGain.length; i++){SuperPowerGain[i]= false; SuperPowerCount[i]=0; DrawablePower[i]=0;}
        for(int i=0; i<isSquized.length; i++){isSquized[i]=false; soundOn[i]=false;}

        thread = new GameThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {

            }
        });

        setFocusable(true); 
    }

	public GameThread getThread() {
		return thread;
	}
	
	

	public class GameThread extends Thread {
		
		  
        public GameThread(SurfaceHolder surfaceHolder, Context context,
                Handler handler) {
            mySurfaceHolder = surfaceHolder;
            myContext = context;
            intro = BitmapFactory.decodeResource(myContext.getResources(), R.drawable.niocoders);
            backgroundImg = BitmapFactory.decodeResource(myContext.getResources(), R.drawable.title);

            backgroundOrigW = backgroundImg.getWidth();
    		backgroundOrigH = backgroundImg.getHeight();
            Play  = BitmapFactory.decodeResource(myContext.getResources(), R.drawable.play);
		    Arrow = BitmapFactory.decodeResource(myContext.getResources(), R.drawable.arrow);
		    Settings = BitmapFactory.decodeResource(myContext.getResources(), R.drawable.sett);
		    SettingMenu =  BitmapFactory.decodeResource(myContext.getResources(), R.drawable.settingmenu);
            Menu = BitmapFactory.decodeResource(myContext.getResources(), R.drawable.menu);
            rabit = BitmapFactory.decodeResource(myContext.getResources(), R.drawable.rabit);
   	        Rabit_Width =  rabit.getWidth() / Rabit_Cols;
   	        Rabit_Height = rabit.getHeight() / Rabit_Rows;
   	        
 	       MainCharacter[0]=BitmapFactory.decodeResource(myContext.getResources(),R.drawable.rabit2);
 	       MainCharacter[1]=BitmapFactory.decodeResource(myContext.getResources(),R.drawable.rabit3);
 	       MainCharacter[2]=BitmapFactory.decodeResource(myContext.getResources(),R.drawable.rabit4);
 	       MainCharacter[3]=BitmapFactory.decodeResource(myContext.getResources(),R.drawable.rabit5);
 	       MainCharacter[4]=BitmapFactory.decodeResource(myContext.getResources(),R.drawable.rabit6);
            
        }
        
        @Override
        public void run() {
            while (running) {
                Canvas c = null;
                try {
                    c = mySurfaceHolder.lockCanvas(null);
                    synchronized(mySurfaceHolder){
                    
                    		
                        AnimateRabit(); //while is running call animate rabit 
                        draw(c); //draw attributes to our game character
                       // drawPower(c);//draw power list 
                        drawSetting(c);
                       
                    }
                  
                } finally {
                    if (c != null) {
                        mySurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
        
        
       private void drawSetting(Canvas c)
       {
    	   if(showSettings){
    	      c.drawBitmap(SettingMenu, ( screenW-SettingMenu.getWidth()),100, null);
    	   }
       }
  
        private void drawPresentation(final Canvas c)
        {
        	if(onIntro){
        		 c.drawBitmap(intro, 0, 0, null);
        		 
        		 Runnable task = new Runnable() {
        			    public void run() {
        			        onIntro =false;
        			        onTitle = true;
        			      
        			    }
        			  };
        			  booleanAtrs.schedule(task, 5, TimeUnit.SECONDS);
        	}
        }
        
        private void drawTitle(Canvas c)
        {
        	if(onTitle)
        	{
            	c.drawBitmap(backgroundImg, 0,0,null);  
            	Runnable task = new Runnable()
            	{
            		 public void run()
            		 {
            			 onIntro = false;
            			 onTitle = false;
            			 canMove = true;
            			 onMenu = true;
            			 
            		 }
            	};
            	booleanAtrs.schedule(task,5, TimeUnit.SECONDS);
            
        	}
        }
        
        private void drawMenu(Canvas c)
        {
        	
        	if(onMenu){
                   
                 	c.drawBitmap(Menu, 0,0,null);
                 	c.drawBitmap(Play, PlayX,showAnimationMenu,null);
                 	if(showAnimationMenu<=PlayY && canMove){showAnimationMenu++;}
                 	else{
                 		canMove=false;
                 		showAnimationMenu=PlayY;
                 		c.drawBitmap(Settings, 0,0,null);}
                 		
                 	
        	}
        }
        private void drawMap(Canvas c)
        {
        	c.drawBitmap(backgroundImg, 0,0,null);
        	
        }
        
        private void drawPowers(Canvas c)
        {
        	if(!onMenu && !onIntro && !onTitle)
        	{
        		int x=200;
        		for(int i=0; i<SuperPower.length-1; i++)
        		{
        			c.drawBitmap(SuperPower[i], x+=100, 10, null);
        			String value=" "+SuperPowerCount[i];
        			c.drawText(value, x+=65,55, backPaint );
        		}
        		
        	}
        }
        
        private void draw(Canvas canvas) {
        	try {
        		drawPresentation(canvas);
        		drawTitle(canvas);
        		drawMenu(canvas);
        		drawScore(canvas);
        		
        	
        		if(!onMenu && !onIntro && !onTitle){
        			showSettings = false;
        			drawMap(canvas);
        			canvas.drawText("Squized : " + Integer.toString(CharacterSquized),
        					10, backPaint.getTextSize()+10, backPaint);
        			canvas.drawText("Missed : " + Integer.toString(CharacterIsMissed),
        					10, backPaint.getTextSize()+70, backPaint);
        			canvas.drawText("Score : " + Integer.toString(Score),screenW-(backPaint.getTextSize()+300),60,backPaint);
        		//drawPowers
        			drawPowers(canvas);
        			
        			//draw character	
        		for(int i=0; i<CharacterPosX.length; i++){
        			if(!ActiveRabit)
        			{drawRabit(canvas,0,0,CharacterPosX[i],CharacterPosY[i] );
        			}
				      
			       }
        		
        		 //show Active Rabit after Y values Change
        		if(ActiveRabit){
        		        ShowActiveRabit(canvas);
        			}
        		 
        		//draw front mask
        		 canvas.drawBitmap(FrontMask, 0,YMaskValue,null);
        		 
        		}
       	
        		if (isSquized[0] ||isSquized[1] || isSquized[2] ||isSquized[3] ||
        				isSquized[4] || isSquized[5] || isSquized[6]) {
        			//ShowJustHit(canvas);
       
        			drawScoreAnimation(canvas);
        		}
        			
        	  
        	} catch (Exception e) {
        	}
        	
        }
       
       private void updateScore()
       {
    	   ScoreYDirection-=5;
       }
       
       private void drawScore(final Canvas canvas)
       {
    	   if(DrawScore)
    	   {
    		 
    		 font = Typeface.createFromAsset(myContext.getAssets(), "font.ttf");
  			 backPaint =  new Paint();
  			 backPaint.setStyle(Style.FILL);
  			 backPaint.setTextAlign(Paint.Align.LEFT);
  			 backPaint.setColor(Color.WHITE);
  			 backPaint.setTypeface(font);
  			 backPaint.setTextSize(drawScaleWidth *10);
  			 backPaint.setAntiAlias(true);
  			 
  			 canvas.drawBitmap(GameOver ,(screenW/2)-GameOver.getWidth()/2,screenH/2-GameOver.getHeight(), null);
  			 if(animatedScore<Score){
    		  
    	       		
    	      canvas.drawText("Score :"+Integer.toString(animatedScore++),((screenW/2)+(GameOver.getWidth()/2)),((screenH/2)+(GameOver.getHeight()/2)),backPaint);
    	       	
  			 }else{
  				 DrawScore = false;
  				 onMenu = true;
  				 Score=0;
  			 }
    	   }
    	   
    	   
    	   
       }
      
       private void drawScoreAnimation(Canvas canvas)
       {
    	   if(isSquized[0])
    	   {
    	   canvas.drawBitmap(Scores,ScoreXDirection, ScoreYDirection, null);
    	   updateScore();
    	   Runnable task = new Runnable()
       	{
       		 public void run()
       		 {
       			isSquized[0] = false;
       		 }
       	};
       	booleanAtrs.schedule(task,1, TimeUnit.SECONDS);
    	   
    	   }
    	   
    	   if(isSquized[1])
    	   {
    	   canvas.drawBitmap(Scores,ScoreXDirection, ScoreYDirection, null);
    	   updateScore();
    	   Runnable task = new Runnable()
       	{
       		 public void run()
       		 {
       			isSquized[1] = false;
       		 }
       	};
       	booleanAtrs.schedule(task,1, TimeUnit.SECONDS);
    	   
    	   }
    	   if(isSquized[2])
    	   {
    	   canvas.drawBitmap(Scores,ScoreXDirection, ScoreYDirection, null);
    	   updateScore();
    	   Runnable task = new Runnable()
       	{
       		 public void run()
       		 {
       			isSquized[2] = false;
       		 }
       	};
       	booleanAtrs.schedule(task,1, TimeUnit.SECONDS);
    	   
    	   }
    	   if(isSquized[3])
    	   {
    	   canvas.drawBitmap(Scores,ScoreXDirection, ScoreYDirection, null);
    	   updateScore();
    	   Runnable task = new Runnable()
       	{
       		 public void run()
       		 {
       			isSquized[3] = false;
       		 }
       	};
       	booleanAtrs.schedule(task,1, TimeUnit.SECONDS);
    	   
    	   }
    	   if(isSquized[4])
    	   {
    	   canvas.drawBitmap(Scores,ScoreXDirection, ScoreYDirection, null);
    	   updateScore();
    	   Runnable task = new Runnable()
       	{
       		 public void run()
       		 {
       			isSquized[4] = false;
       		 }
       	};
       	booleanAtrs.schedule(task,1, TimeUnit.SECONDS);
    	   
    	   }
    	   if(isSquized[5])
    	   {
    	   canvas.drawBitmap(Scores,ScoreXDirection, ScoreYDirection, null);
    	   updateScore();
    	   Runnable task = new Runnable()
       	{
       		 public void run()
       		 {
       			isSquized[5] = false;
       		 }
       	};
       	booleanAtrs.schedule(task,1, TimeUnit.SECONDS);
    	   
    	   }
    	   if(isSquized[6])
    	   {
    	   canvas.drawBitmap(Scores,ScoreXDirection, ScoreYDirection, null);
    	   updateScore();
    	   Runnable task = new Runnable()
       	{
       		 public void run()
       		 {
       			isSquized[6] = false;
       		 }
       	};
       	booleanAtrs.schedule(task,1, TimeUnit.SECONDS);
    	   
    	   }
       }
        //Rabit Attributes 
        private void drawRabit(Canvas canvas,int row, int col, int x, int y) {
          
            int X = row * Rabit_Width ;
            int Y = col * Rabit_Height;
            Rect src = new Rect(X,Y,X+Rabit_Width, Y+Rabit_Height);
            Rect dst = new Rect(x,y,x+Rabit_Width , y+Rabit_Height);
            canvas.drawBitmap(rabit, src, dst, null);
           
      }


        
        private void ShowActiveRabit(Canvas c)
        {
        	 if(CharacterPosY[0]<=400)
    		 {
        		 if(soundOn[0]){
 					AudioManager audioManager = (AudioManager)
 					myContext.getSystemService
 					(Context.AUDIO_SERVICE);
 					float volume = (float)
 					audioManager.getStreamVolume
 					(AudioManager.STREAM_MUSIC);
 					sounds.play(Here, volume, volume, 1, 0,
 					1);
         		}
        	
        			drawRabit(c,1,0, CharacterPosX[0]-45,CharacterPosY[0] );
        		    
        			
    		 }
        	
        	 if(CharacterPosY[1]<=400 )
    		 {
        		 
        		   
        			drawRabit(c,1,0, CharacterPosX[1]-45,CharacterPosY[1] );
    		 }
        	 if(CharacterPosY[2]<=400 )
    		 {
        		 if(soundOn[2]){
 					AudioManager audioManager = (AudioManager)
 					myContext.getSystemService
 					(Context.AUDIO_SERVICE);
 					float volume = (float)
 					audioManager.getStreamVolume
 					(AudioManager.STREAM_MUSIC);
 					sounds.play(Here, volume, volume, 1, 0,
 					1);
         		}
        		
     			drawRabit(c,1,0, CharacterPosX[2]-45,CharacterPosY[2] );
 		     }
        	 
        	 if(CharacterPosY[3]<=400 )
    		 {
        		 
        	
        			drawRabit(c,1,0, CharacterPosX[3]-45,CharacterPosY[3] );
    		 }
        	 if(CharacterPosY[4]<=400)
    		 {
        		 if(soundOn[4]){
 					AudioManager audioManager = (AudioManager)
 					myContext.getSystemService
 					(Context.AUDIO_SERVICE);
 					float volume = (float)
 					audioManager.getStreamVolume
 					(AudioManager.STREAM_MUSIC);
 					sounds.play(Here, volume, volume, 1, 0,
 					1);
         		}
        		
     			drawRabit(c,1,0, CharacterPosX[4]-45,CharacterPosY[4] );
 		     }
        	 if(CharacterPosY[5]<=400 )
    		 {
        		 
        		
        		drawRabit(c,1,0, CharacterPosX[5]-45,CharacterPosY[5] );
    		 }
        	 if(CharacterPosY[6]<=400 )
    		 {
        		 if(soundOn[6]){
  					AudioManager audioManager = (AudioManager)
  					myContext.getSystemService
  					(Context.AUDIO_SERVICE);
  					float volume = (float)
  					audioManager.getStreamVolume
  					(AudioManager.STREAM_MUSIC);
  					sounds.play(Here, volume, volume, 1, 0,
  					1);
          		}
     			drawRabit(c,1,0, CharacterPosX[6]-45,CharacterPosY[6] );
 		     }
        }
           private void pickActiveCharacter() {
        	if (!CharacterHit && ActiveCharacter > 0) {
        		CharacterIsMissed++;
        		if(CharacterIsMissed>5)
				   {
					  DrawScore =  true;
					 
				   }else{ DrawScore=false;}
        		}
        	ActiveCharacter = new Random().nextInt(7) + 1;
        	CharacterIsRaising = true;
        	CharacterIsFalling = false;
        	CharacterHit = false;
        	CharacterRate =  5 + (int)(CharacterSquized /15); //this is the speed of our character
        }
        
        private boolean DetectContact()
        {
        	boolean Contact = false;
        	if(ActiveCharacter == 1 &&
        	   FingerX >= CharacterPosX[0] && FingerX < CharacterPosX[0] + (int)(88 * drawScaleWidth) &&
        	   FingerY > CharacterPosY[0] && FingerY < CharacterPosY[0] + (int)(450 * drawScaleHeight)){
        		CharacterHit = true;
        		Contact = true;
        		isSquized[0] =true;
        		ScoreXDirection =  CharacterPosX[0];
        		ScoreYDirection =  CharacterPosY[0]-50;
            }
        	
        	if(ActiveCharacter == 2 &&
             	   FingerX >= CharacterPosX[1] && FingerX < CharacterPosX[1] + (int)(88 * drawScaleWidth) &&
             	   FingerY > CharacterPosY[1] && FingerY < CharacterPosY[1] + (int)(400 * drawScaleHeight)){
             		CharacterHit = true;
             		isSquized[1] =true;
             		Contact = true;
             		ScoreXDirection =  CharacterPosX[1];
            		ScoreYDirection =  CharacterPosY[1]-50;
                 }
        	if(ActiveCharacter == 3 &&
             	   FingerX >= CharacterPosX[2] && FingerX < CharacterPosX[2] + (int)(88 * drawScaleWidth) &&
             	   FingerY > CharacterPosY[2] && FingerY < CharacterPosY[2] + (int)(450 * drawScaleHeight)){
             		CharacterHit = true;
             		isSquized[2] =true;
             		Contact = true;
             		ScoreXDirection =  CharacterPosX[2];
            		ScoreYDirection =  CharacterPosY[2]-50;
                 }
        	if(ActiveCharacter == 4 &&
              	   FingerX >= CharacterPosX[3] && FingerX < CharacterPosX[3] + (int)(88 * drawScaleWidth) &&
              	   FingerY > CharacterPosY[3] && FingerY < CharacterPosY[3] + (int)(400 * drawScaleHeight)){
              		CharacterHit = true;
              		isSquized[3] =true;
              		Contact = true;
              		ScoreXDirection =  CharacterPosX[3];
            		ScoreYDirection =  CharacterPosY[3]-50;
                  }
        	if(ActiveCharacter == 5 &&
              	   FingerX >= CharacterPosX[4] && FingerX < CharacterPosX[4] + (int)(88 * drawScaleWidth) &&
              	   FingerY > CharacterPosY[4] && FingerY < CharacterPosY[4] + (int)(450 * drawScaleHeight)){
              		CharacterHit = true;
              		isSquized[4] =true;
              		Contact = true;
              		ScoreXDirection =  CharacterPosX[4];
            		ScoreYDirection =  CharacterPosY[4]-50;
                  }
        	if(ActiveCharacter == 6 &&
               	   FingerX >= CharacterPosX[5] && FingerX < CharacterPosX[5] + (int)(88 * drawScaleWidth) &&
               	   FingerY > CharacterPosY[5] && FingerY < CharacterPosY[5] + (int)(400 * drawScaleHeight)){
               		CharacterHit = true;
               		isSquized[5] =true;
               		Contact = true;
               		ScoreXDirection =  CharacterPosX[5];
            		ScoreYDirection =  CharacterPosY[5]-50;
                   }
        	if(ActiveCharacter == 7 &&
               	   FingerX >= CharacterPosX[6] && FingerX < CharacterPosX[6] + (int)(88 * drawScaleWidth) &&
               	   FingerY > CharacterPosY[6] && FingerY < CharacterPosY[6] + (int)(450 * drawScaleHeight)){
               		CharacterHit = true;
               		isSquized[6] =true;
               		Contact = true;
               		ScoreXDirection =  CharacterPosX[6];
            		ScoreYDirection =  CharacterPosY[6]-50;
                   }
        	
        	
        	
        	
        	return Contact;
        }

     
        //Give animation to our Rabit :-)
        private void AnimateRabit()
        {
        	if(ActiveCharacter == 1 )
        	{
        		
        		if(CharacterIsRaising)
        		{
        			CharacterPosY[0] -= CharacterRate;
        			ActiveRabit = true;
        			soundOn[0] =true;
        			
        		}else if(CharacterIsFalling)
        		{
        			CharacterPosY[0] += CharacterRate;
        			soundOn[0]=false;
        			ActiveRabit = false;
        		}
        		
        		//Second Statement
        		if(CharacterPosY[0] >= (int)(475 *drawScaleHeight) || 
        				CharacterHit){
        			CharacterPosY[0] =(int)(475 *drawScaleHeight);
        			pickActiveCharacter();
        		}
        		
        		if(CharacterPosY[0] <= (int)(300 * drawScaleHeight))
        		{
                    CharacterPosY[0] = (int)(300*drawScaleHeight);
                    CharacterIsRaising = false;
                    CharacterIsFalling = true;
        		}
        	}
        	
        	//second Statement 
        	if(ActiveCharacter == 2 )
        	{
        		if(CharacterIsRaising)
        		{
        			CharacterPosY[1] -= CharacterRate;
        			ActiveRabit = true;
        			soundOn[1] =true;
        			
        		}else if(CharacterIsFalling)
        		{
        			CharacterPosY[1] += CharacterRate;
        			ActiveRabit = false;
        			soundOn[1] =false;
        		}
        		
        		//Second Statement
        		if(CharacterPosY[1] >= (int)(425 *drawScaleHeight) || 
        				CharacterHit){
        			CharacterPosY[1] =(int)(425 *drawScaleHeight);
        			pickActiveCharacter();
        		}
        		
        		if(CharacterPosY[1] <= (int)(250 * drawScaleHeight))
        		{
                    CharacterPosY[1] = (int)(250*drawScaleHeight);
                    CharacterIsRaising = false;
                    CharacterIsFalling = true;
        		}
        	}
        	
        	//Third Statement to our character animation
        	if(ActiveCharacter == 3 )
        	{
        		if(CharacterIsRaising)
        		{
        			CharacterPosY[2] -= CharacterRate;
        			ActiveRabit = true;
        			soundOn[2]=true;
        			
        		}else if(CharacterIsFalling)
        		{
        			CharacterPosY[2] += CharacterRate;
        			ActiveRabit = false;
        			soundOn[2] =false;
        		}
        		
        		//Second Statement
        		if(CharacterPosY[2] >= (int)(475 *drawScaleHeight) || 
        				CharacterHit){
        			CharacterPosY[2] =(int)(475 *drawScaleHeight);
        			pickActiveCharacter();
        		}
        		
        		if(CharacterPosY[2] <= (int)(300 * drawScaleHeight))
        		{
                    CharacterPosY[2] = (int)(300*drawScaleHeight);
                    CharacterIsRaising = false;
                    CharacterIsFalling = true;
        		}
        	}
        	
        	//Four Statement to our character animation
        	if(ActiveCharacter == 4 )
        	{
        		if(CharacterIsRaising)
        		{
        			CharacterPosY[3] -= CharacterRate;
        			ActiveRabit =  true;
        			soundOn[3] = true;
        		}else if(CharacterIsFalling)
        		{
        			CharacterPosY[3] += CharacterRate;
        			ActiveRabit = false;
        			soundOn[3]= false;
        		}
        		
        		//Second Statement
        		if(CharacterPosY[3] >= (int)(425 *drawScaleHeight) || 
        				CharacterHit){
        			CharacterPosY[3] =(int)(425 *drawScaleHeight);
        			pickActiveCharacter();
        		}
        		
        		if(CharacterPosY[3] <= (int)(250 * drawScaleHeight))
        		{
                    CharacterPosY[3] = (int)(250*drawScaleHeight);
                    CharacterIsRaising = false;
                    CharacterIsFalling = true;
        		}
        	}
        	
        	  //Five statement to our Character animation
        	if(ActiveCharacter == 5 )
        	{
        		if(CharacterIsRaising)
        		{
        			CharacterPosY[4] -= CharacterRate;
        			ActiveRabit = true;
        			soundOn[4] =true;
        			
        		}else if(CharacterIsFalling)
        		{
        			CharacterPosY[4] += CharacterRate;
        			ActiveRabit = false;
        			soundOn[4]=false;
        		}
        		
        		
        		if(CharacterPosY[4] >= (int)(475 *drawScaleHeight) || 
        				CharacterHit){
        			CharacterPosY[4] =(int)(475 *drawScaleHeight);
        			pickActiveCharacter();
        		}
        		
        		if(CharacterPosY[4] <= (int)(300 * drawScaleHeight))
        		{
                    CharacterPosY[4] = (int)(300*drawScaleHeight);
                    CharacterIsRaising = false;
                    CharacterIsFalling = true;
        		}
        	}
        	   //six stament to our Character animation
        	if(ActiveCharacter == 6 )
        	{
        		if(CharacterIsRaising)
        		{
        			CharacterPosY[5] -= CharacterRate;
        			ActiveRabit = true;
        			soundOn[5] = true;
        			
        		}else if(CharacterIsFalling)
        		{
        			CharacterPosY[5] += CharacterRate;
        			ActiveRabit = false;
        			soundOn[5] = false;
        		}
        		
        		//Second Statement
        		if(CharacterPosY[5] >= (int)(425 *drawScaleHeight) || 
        				CharacterHit){
        			CharacterPosY[5] =(int)(425 *drawScaleHeight);
        			pickActiveCharacter();
        		}
        		
        		if(CharacterPosY[5] <= (int)(250 * drawScaleHeight))
        		{
                    CharacterPosY[5] = (int)(250*drawScaleHeight);
                    CharacterIsRaising = false;
                    CharacterIsFalling = true;
        		}
        	}
      	  //Seven statement to our Character animation
        	if(ActiveCharacter == 7 )
        	{
        		if(CharacterIsRaising)
        		{
        			CharacterPosY[6] -= CharacterRate;
        			ActiveRabit = true;
        			soundOn[6]=true;
        			
        		}else if(CharacterIsFalling)
        		{
        			CharacterPosY[6] += CharacterRate;
        			ActiveRabit = false;
        			soundOn[6] =false;
        		}
        		
        		
        		if(CharacterPosY[6] >= (int)(475 *drawScaleHeight) || 
        				CharacterHit){
        			CharacterPosY[6] =(int)(475 *drawScaleHeight);
        			pickActiveCharacter();
        		}
        		
        		if(CharacterPosY[6] <= (int)(300 * drawScaleHeight))
        		{
                    CharacterPosY[6] = (int)(300*drawScaleHeight);
                    CharacterIsRaising = false;
                    CharacterIsFalling = true;
        		}
        	}
        	
        	
        }
        
       
        private void RetrieveImageFactory()
        {
        	   backgroundImg = BitmapFactory.decodeResource(myContext.getResources(),R.drawable.map1);
		       backgroundImg = Bitmap.createScaledBitmap(backgroundImg,screenW, screenH,true);
		       FrontMask = BitmapFactory.decodeResource(myContext.getResources(),com.agpfd.whackamole.R.drawable.mask);
		       GameOver =  BitmapFactory.decodeResource(myContext.getResources(), R.drawable.gameover);
		       ScaleWidth = (float) screenW / (float) backgroundOrigW;
		       ScaleHeight = (float)screenH / (float) backgroundOrigH;
		       
		       FrontMask = Bitmap.createScaledBitmap(FrontMask,(int)(FrontMask.getWidth()*ScaleWidth),
		    		   (int)(FrontMask.getHeight()*ScaleHeight), true);
		       GameOver =  Bitmap.createScaledBitmap(GameOver, (int)(GameOver.getWidth()*ScaleWidth),
		    		   (int)(GameOver.getHeight()*ScaleHeight), true);
		      
		       
		       
		       
		       Scores = BitmapFactory.decodeResource(myContext.getResources(), R.drawable.coin);
		       Scores  =Bitmap.createScaledBitmap(Scores, (int)(Scores.getWidth() * ScaleWidth),
		    		   (int)(Scores.getHeight() * ScaleHeight), true);
		      
		       
		       //Drawable powers
		       DrawablePower[0]=(int)(R.drawable.bombs);
		       DrawablePower[1]=(int)(R.drawable.rocket);
		       DrawablePower[2]=(int)(R.drawable.clock);
		       DrawablePower[3]=(int)(R.drawable.twox);
		       
		     
		       for(int i=0; i<SuperPower.length-1; i++)
		       {
		    	   SuperPower[i] = BitmapFactory.decodeResource(myContext.getResources(), DrawablePower[i]);
		    	   SuperPower[i] = Bitmap.createScaledBitmap(SuperPower[i],(int)(SuperPower[i].getWidth()*ScaleWidth),
		    			   (int)(SuperPower[i].getHeight()*ScaleHeight),true);
		       }
		       
		     
		      
        }
        
        boolean doTouchEvent(MotionEvent event)
    	{
    		synchronized(mySurfaceHolder)
    		{
    			int handlerEvent = event.getAction();
    			int x = (int)event.getX();
    			int y = (int)event.getY();
    			switch(handlerEvent)
    			{
    			   case MotionEvent.ACTION_UP:
    				   
    				 if(onMenu && !onTitle && !onIntro){
    					 RetrieveImageFactory();
    					Toast.makeText(myContext.getApplicationContext(),"Screen Width = "+screenW+" ScreenH ="+screenH, Toast.LENGTH_LONG).show();
    					
    				   if(x>=0 && y>=0 && x<=100 && y<=95)
    					 {
    					   if(showSettings)
    					   {
    						   showSettings = false;
    					   }else{
    						   showSettings = true;
    					   }
    					   Toast.makeText(getContext(),"Settings Button Pressed", Toast.LENGTH_LONG).show();
    						 
    					 }
    				   if(showSettings)
    				   {
    					  if(x>=1115 && y>=240 && x<=1140 && y<=260)
    					  {
    						  Toast.makeText(getContext(),"Music Button Pressed", Toast.LENGTH_LONG).show();
    					  }
    					  if(x>=1115 && y>=320 && x<=1140 && y<=360)
    					  {
    						  Toast.makeText(getContext(),"Sound Button Pressed", Toast.LENGTH_LONG).show();
    					  }
    					   
    				   }
    				
    				   if(x>=104 && y>=180 && x<=380 && y<=245)
    				   {
    					   Toast.makeText(getContext(),"Start Button Pressed", Toast.LENGTH_LONG).show();
    					  
    				       onMenu = false;
    					   pickActiveCharacter();
    					   
    				   }
    				   if(x>=104 && y>=310 && x<=380 && y<=345)
    				   {
    					   Toast.makeText(getContext(),"Score Button Pressed", Toast.LENGTH_LONG).show();
    				   }
    				   if(x>=104 && y>=400 && x<=380 && y<=430)
    				   {
    					   Toast.makeText(getContext(),"Powers Button Pressed", Toast.LENGTH_LONG).show();
    				   }
    				   if(x>=104 && y>=480 && x<=380 && y<=490)
    				   {
    					   Toast.makeText(getContext(),"Exit Button Pressed", Toast.LENGTH_LONG).show();
    				   }
    				   
    				 
    				 } 
    			   
    				  
                       break;
    			   case MotionEvent.ACTION_DOWN:
    				   FingerX = x;
    				   FingerY = y;
    				   if(!onMenu && DetectContact())
    				   {
    					   Random rand = new Random();
    					   int num = rand.nextInt(2)+0;
    					   
    					   if(soundOn[0] || soundOn[1] ||soundOn[2] ||soundOn[3] ||
    							   soundOn[4] ||soundOn[5] ||soundOn[6]){
    						  
    					   }
    					   
    					   
    					   CharacterSquized++;
    					   Score+=10;
    				
    				   }
    				   
    				 
    				  
    				   break;
    			   case MotionEvent.ACTION_MOVE:
    				   
    				   break;
    			}
    			
    			return true;
    			
    		}
    		
    	}
        
        public void setSurfaceSize(int width, int height)
    	{
    		synchronized(mySurfaceHolder)
    		{
    			screenW = width;
    			screenH = height;
    			backgroundImg = Bitmap.createScaledBitmap(backgroundImg, width, height, true);
    			intro = Bitmap.createScaledBitmap(intro,width, height, true);
    			Menu = Bitmap.createScaledBitmap(Menu, width,height, true);
    			float scalewidth = (float)screenW / 800;
    			float scaleheight = (float)screenH / 600;
    			if(screenW>=1280 && screenH >720){
    			
    			 SettingMenu = Bitmap.createScaledBitmap(SettingMenu, (int) (SettingMenu.getWidth()+200*scalewidth), (int)(SettingMenu.getHeight()+200 * scaleheight), true);
    			 
    		     Settings = Bitmap.createScaledBitmap(Settings, (int) (Settings.getWidth()+10*scalewidth), (int)(Settings.getHeight()+10 * scaleheight), true);
    			 Arrow = Bitmap.createScaledBitmap(Arrow, (int)(Arrow.getWidth()+100*scalewidth), (int)(Arrow.getHeight()*scaleheight), true);
    		  	 Play = Bitmap.createScaledBitmap(Play, (int)(Play.getWidth()*scalewidth),(int)(Play.getHeight()*scaleheight), true);
    			 PlayX = 0;
    			 PlayY = screenH - 600;
    			 showAnimationMenu= 0;
    			}else{
    				 Settings = Bitmap.createScaledBitmap(Settings, (int) (Settings.getWidth()*scalewidth), (int)(Settings.getHeight() * scaleheight), true);
    				 Arrow = Bitmap.createScaledBitmap(Arrow, (int)(Arrow.getWidth()-300*scalewidth), (int)(Arrow.getHeight()-200*scaleheight), true);
        		  	 Play = Bitmap.createScaledBitmap(Play, (int)(Play.getWidth()-300*scalewidth),(int)(Play.getHeight()-200*scaleheight), true);
        			 PlayX = 0;
        			 PlayY = screenH -500;
        			 showAnimationMenu=0;
        			 for(int i=0; i<MainCharacter.length; i++)
        			 {
        				 MainCharacter[i]= Bitmap.createScaledBitmap(MainCharacter[i],(int)(MainCharacter[i].getWidth()*scalewidth),
        						 (int)(MainCharacter[i].getHeight()*scaleheight),true);
        			 }
    			}
    			drawScaleWidth = (float) screenW / 800;
    			drawScaleHeight = (float) screenH / 600;
    			CharacterPosX[0] = (int)(drawScaleWidth * 35);
    			CharacterPosX[1] = (int)(drawScaleWidth * 135);
    			CharacterPosX[2] = (int)(drawScaleWidth * 235);
    			CharacterPosX[3] = (int)(drawScaleWidth * 335);
    			CharacterPosX[4] = (int)(drawScaleWidth * 435);
    			CharacterPosX[5] = (int)(drawScaleWidth * 535);
    			CharacterPosX[6] = (int)(drawScaleWidth * 635);
    			
    			//Y postion of our character in the map

    			CharacterPosY[0] = (int)(drawScaleHeight * 475);
    			CharacterPosY[1] = (int)(drawScaleHeight * 425);
    			CharacterPosY[2] = (int)(drawScaleHeight * 475);
    			CharacterPosY[3] = (int)(drawScaleHeight * 425);
    			CharacterPosY[4] = (int)(drawScaleHeight * 475);
    			CharacterPosY[5] = (int)(drawScaleHeight * 425);
    			CharacterPosY[6] = (int)(drawScaleHeight * 475);
    			
    			//Character Front MAsk X positions
    			 CharacterMasPosX[0] =   (int)(50*drawScaleWidth);
    			 CharacterMasPosX[1] =   (int)(150*drawScaleWidth);
    			 CharacterMasPosX[2] =   (int)(250*drawScaleWidth);
    			 CharacterMasPosX[3] =   (int)(350*drawScaleWidth);
    			 CharacterMasPosX[4] =   (int)(450*drawScaleWidth);
    			 CharacterMasPosX[5] =   (int)(550*drawScaleWidth);
    			 CharacterMasPosX[6] =   (int)(650*drawScaleWidth);
    			
    			 CharacterMasPosY[0] =   (int)(450*drawScaleHeight);
    			 YMaskValue   = (int)(373*drawScaleHeight);
    			 CharacterMasPosY[1] =   (int)(400*drawScaleHeight);
    			 CharacterMasPosY[2] =   (int)(450*drawScaleHeight);
    			 CharacterMasPosY[3] =   (int)(400*drawScaleHeight);
    			 CharacterMasPosY[4] =   (int)(450*drawScaleHeight);
    			 CharacterMasPosY[5] =   (int)(400*drawScaleHeight);
    			 CharacterMasPosY[6] =   (int)(450*drawScaleHeight);
    			 
    			
    			 
    			 //Font attributes 
    			 font = Typeface.createFromAsset(myContext.getAssets(), "font.ttf");
    			 backPaint =  new Paint();
    			 backPaint.setStyle(Style.FILL);
    			 backPaint.setTextAlign(Paint.Align.LEFT);
    			 backPaint.setColor(Color.RED);
    			 backPaint.setTypeface(font);
    			 backPaint.setTextSize(drawScaleWidth *30);
    			 backPaint.setAntiAlias(true);
    		}
    	}
        
        
        
        public void setRunning(boolean b) {
            running = b;
        }

	
        
       
    }
	
	
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return thread.doTouchEvent(event);
    }
	
   
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		thread.setSurfaceSize(width, height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder){
		thread.setRunning(true);
		if (thread.getState() == Thread.State.NEW ){
			thread.start();
		}
	}
	
	@Override
    public void surfaceDestroyed(SurfaceHolder holder) {
     
      boolean rePeat = true;
		thread.setRunning(false);
	
		while(rePeat)
		{
			try{
			thread.join();
			rePeat = false;
			}catch(InterruptedException e){ }
		}
		
    }
	
	
}

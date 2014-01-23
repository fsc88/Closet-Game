package com.example.closet;


import java.util.ArrayList;
import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSCounter;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;


import org.andengine.util.HorizontalAlign;
import org.andengine.util.SAXUtils;
import org.andengine.util.color.Color;
import org.xml.sax.Attributes;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.example.closet.BaseScene;
import com.example.closet.SceneManager.SceneType;


public class GameScene extends BaseScene{
	
	
	private ArrayList<Cloth> ListOfCloth;
	
	
	private int count = 0; //Conta el numero de roba que s'ha guardat
	private final int MAX_CLOTH = 100;
	private final int VISIBLE_CLOTH = 5;
	private final int CLOTH_SIZE = 150;
	private final int NUM_LIFES=3;
	
	private int combo = 0;
	private final int MAX_COM = 5;
	private final int LONG_COM = 10;
	private int time = 60;
	
	Random randomInt = new Random();
	
	Sprite level_end;
	Sprite menu_pause;
	
	private boolean pause = false;
	
	private Lifes lifes;
	
	
	@Override
	public void createScene() {
		// TODO Auto-generated method stub
		createBackground();
		lifes = new Lifes(vbom,GameScene.this,NUM_LIFES);
	    createPhysics();
	    createPauseMenu();
	    createLevelComplete();
	    ListOfCloth =  new ArrayList<Cloth>();
	    int par = -1;
	    for(int i = 0; i < MAX_CLOTH; i++){
	    	ListOfCloth.add(i,createItems((MAX_CLOTH-1)-i,0,par));
	    	par = par*(-1);
	    }
	    createControllers();
	    createChrono();
	    SceneManager.getInstance().getCurrentScene().sortChildren();
	    
	    //createHUD();
	}

	@Override
	public void onBackKeyPressed() {
		// TODO Auto-generated method stub
		//SceneManager.getInstance().loadMenuScene(engine);
		
		if(pause == false) showMenuPause();
		
		
	}
	
	
	

	@Override
	public SceneType getSceneType() {
		// TODO Auto-generated method stub
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene() {
		camera.setHUD(null);
	    //camera.setCenter(0, 0);

	    // TODO code responsible for disposing scene
	    // removing all game scene objects.
		
	}
	
	private void createBackground()
	{
	    attachChild(new Sprite(0, 0, resourcesManager.game_background, vbom)
	    {
	        @Override
	        protected void preDraw(GLState pGLState, Camera pCamera) 
	        {
	            super.preDraw(pGLState, pCamera);
	            pGLState.enableDither();
	        }
	    });
	}
	
	//private HUD gameHUD;
	private Text scoreText;
	private Text resum;

	
	
	private int score = 0;

	private void addToScore(int i)
	{
	    score += i;
	    scoreText.setText("Score: " + score);
	}
	
	private Text chrono;
	
	private void createChrono(){
	 
		TimerHandler spriteTimerHandler;
	    float mEffectSpawnDelay = 1f;
	    
	 
	    spriteTimerHandler = new TimerHandler(mEffectSpawnDelay, true,
	    new ITimerCallback() {
	    	
	        @Override
	        public void onTimePassed(TimerHandler pTimerHandler) {
	        	if(pause == false){
	        		chrono.setText("Time: " + time);
	        		if(time <= 0){
	        			pause = true;
		  	            game_over();
	        		}
	        		time--;
	        	}
	        	
	        }
	    });
	    
	    engine.registerUpdateHandler(spriteTimerHandler);
	
	}
	
	private void setScore(){
		 
		TimerHandler spriteTimerHandler;
	    float mEffectSpawnDelay = 0.01f;
	    
	 
	    spriteTimerHandler = new TimerHandler(mEffectSpawnDelay, true,
	    new ITimerCallback() {
	    	int incres = 0;
	        @Override
	        public void onTimePassed(TimerHandler pTimerHandler) {
	        	
	        	if(incres <= score*time){
	        		endScoreText.setText("Score: " + incres);
	        		incres++;
	        	}
	        	
	        }
	    });
	    
	    engine.registerUpdateHandler(spriteTimerHandler);
	    
	
	}
	
	private void createLevelComplete(){
		
		level_end=new Sprite(500, 100, resourcesManager.level_complete_region, vbom)
	    {
	        @Override
	        protected void preDraw(GLState pGLState, Camera pCamera) 
	        {
	            super.preDraw(pGLState, pCamera);
	            pGLState.enableDither();
	        }
	    };
		
		attachChild(level_end);
	}
	
	private void createPauseMenu(){
		
		menu_pause=new Sprite(40, 100, resourcesManager.level_complete_region, vbom)
	    {
	        @Override
	        protected void preDraw(GLState pGLState, Camera pCamera) 
	        {
	            super.preDraw(pGLState, pCamera);
	            pGLState.enableDither();
	        }
	        
	        
		        public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y)
		        {

		            if (touchEvent.isActionUp())
		            {
		            	SceneManager.getInstance().loadMenuScene(engine);
		            }
		            
		            if (touchEvent.isActionDown()){
		            	
		            }
		            
		            return true;
		        };

	    };
		
	    //camera.getHUD().registerTouchArea(menu_pause);
	    menu_pause.setVisible(false);
		attachChild(menu_pause);
	}
	
	private void checkPos(){
		 
		TimerHandler spriteTimerHandler;
	    float mEffectSpawnDelay = 0.1f;
	    
	 
	    spriteTimerHandler = new TimerHandler(mEffectSpawnDelay, true,
	    new ITimerCallback() {
	        @Override
	        public void onTimePassed(TimerHandler pTimerHandler) {
	        	for(int i = 0; i < MAX_CLOTH; i++){
	    	    	if(!ListOfCloth.get(i).isDisposed()){
	    	    		if(ListOfCloth.get(i).getX() < 0 || ListOfCloth.get(i).getX() > 400){
	    	    			ListOfCloth.get(i).detachSelf();
	    	    			ListOfCloth.get(i).dispose();
	    	    		}
	    	    	}
	    	    }
	        }
	    });
	    
	    engine.registerUpdateHandler(spriteTimerHandler);
	    
	
	}
		
	
	private PhysicsWorld physicsWorld;

	private void createPhysics()
	{
	    physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, -17), false); 
	    registerUpdateHandler(physicsWorld);
	}
	
	
	
	
	
	
	
	private Cloth createItems(int pos, int current, int par)
	{
		Cloth cloth = null;
		
		Random generator = new Random();
		int randomIndex;
		int randomCloth;
		
		    randomIndex = generator.nextInt(2);    
		    if(randomIndex == 0) 
		    {
		    	randomCloth = generator.nextInt(4);
		    	switch(randomCloth){
		    	  case 0:
		    		  cloth = new Cloth(0, 0,CLOTH_SIZE,CLOTH_SIZE, resourcesManager.hombre1, vbom){
						    @Override
						    protected void preDraw(GLState pGLState, Camera pCamera) 
						    {
						       super.preDraw(pGLState, pCamera);
						       pGLState.enableDither();
						    }
						    
						    
						};
						break;
		    	  case 1:
		    		  cloth = new Cloth(0, 0,CLOTH_SIZE,CLOTH_SIZE, resourcesManager.hombre2, vbom){
						    @Override
						    protected void preDraw(GLState pGLState, Camera pCamera) 
						    {
						       super.preDraw(pGLState, pCamera);
						       pGLState.enableDither();
						    }
						};
						break;
		    	  case 2:
		    		  cloth = new Cloth(0, 0,CLOTH_SIZE,CLOTH_SIZE, resourcesManager.hombre3, vbom){
						    @Override
						    protected void preDraw(GLState pGLState, Camera pCamera) 
						    {
						       super.preDraw(pGLState, pCamera);
						       pGLState.enableDither();
						    }
						};
						break;
		    	  case 3:
		    		  cloth = new Cloth(0, 0,CLOTH_SIZE,CLOTH_SIZE, resourcesManager.hombre4, vbom){
						    @Override
						    protected void preDraw(GLState pGLState, Camera pCamera) 
						    {
						       super.preDraw(pGLState, pCamera);
						       pGLState.enableDither();
						    }
						};
						break;
		    	}
		    				    	
		
	    	}
		    else if(randomIndex == 1)
		    {
		    	randomCloth = generator.nextInt(4);
		    	switch(randomCloth){
		    	  case 0:
		    		  cloth = new Cloth(0, 0,CLOTH_SIZE,CLOTH_SIZE, resourcesManager.mujer1, vbom){
						    @Override
						    protected void preDraw(GLState pGLState, Camera pCamera) 
						    {
						       super.preDraw(pGLState, pCamera);
						       pGLState.enableDither();
						    }
						};
						break;
		    	  case 1:
		    		  cloth = new Cloth(0, 0,CLOTH_SIZE,CLOTH_SIZE, resourcesManager.mujer2, vbom){
						    @Override
						    protected void preDraw(GLState pGLState, Camera pCamera) 
						    {
						       super.preDraw(pGLState, pCamera);
						       pGLState.enableDither();
						    }
						};
						break;
		    	  case 2:
		    		  cloth = new Cloth(0, 0,CLOTH_SIZE,CLOTH_SIZE, resourcesManager.mujer3, vbom){
						    @Override
						    protected void preDraw(GLState pGLState, Camera pCamera) 
						    {
						       super.preDraw(pGLState, pCamera);
						       pGLState.enableDither();
						    }
						};
						break;
		    	  case 3:
		    		  cloth = new Cloth(0, 0,CLOTH_SIZE,CLOTH_SIZE, resourcesManager.mujer4, vbom){
						    @Override
						    protected void preDraw(GLState pGLState, Camera pCamera) 
						    {
						       super.preDraw(pGLState, pCamera);
						       pGLState.enableDither();
						    }
						};
						break;
		    	}
		    	
		    	
		    	
		    }
			 
		    
		    cloth.setUserData(randomIndex);
		    cloth.setPosition(180, pos*100-(MAX_CLOTH-VISIBLE_CLOTH)*100);
		    cloth.setZIndex(MAX_CLOTH-pos);
	    	cloth.setMovement(par*(-45), par*45);
	    	attachChild(cloth);
		    
		    return cloth;
		
		
	}
	
	
	
	
	private MoveByModifier moveLast(boolean direction){
		
		MoveByModifier mod1;
		
		if(direction == true ){ //Left
			mod1 = new MoveByModifier(0.6f, -400, 0){
				@Override
				protected void onModifierFinished(IEntity pItem) {
					// TODO Auto-generated method stub
					super.onModifierFinished(pItem);

					
				}

			}; 
		}
		else{ //Right
			mod1 = new MoveByModifier(0.6f, 400, 0){
				@Override
				protected void onModifierFinished(IEntity pItem) {
					// TODO Auto-generated method stub
					super.onModifierFinished(pItem);

				}

			}; 
		}
		
		return mod1;
	}
	
	Text endScoreText;
	
	private void moveLevelComplete(Sprite level_complete_background){
		
		MoveXModifier mod1;
		
		
			mod1 = new MoveXModifier(0.5f, level_complete_background.getX(), 40){
				@Override
				protected void onModifierFinished(IEntity pItem) {
					// TODO Auto-generated method stub
					super.onModifierFinished(pItem);
					
					
					endScoreText = new Text(200, 400, resourcesManager.font, "Score: 0123456789", new TextOptions(HorizontalAlign.CENTER), vbom);
					endScoreText.setSkewCenter(0, 0);    
					setScore();
					attachChild(endScoreText);
					endScoreText.registerEntityModifier(new LoopEntityModifier(new ScaleModifier(1, 1, 1.3f)));

				}

			}; 
			
			level_complete_background.registerEntityModifier(mod1);
		
	}
	
	private void showMenuPause(){
		
		pause = true;
		
		
		left.setPosition(-200, left.getY());
		right.setPosition(500, right.getY());
		menu_pause.setScale(0);
		menu_pause.setZIndex(1000);
		SceneManager.getInstance().getCurrentScene().sortChildren();
		menu_pause.setVisible(true);
		
		menu_pause.registerEntityModifier(new ScaleModifier(0.5f, 0, 1f){
			@Override
			protected void onModifierFinished(IEntity pItem) {
				// TODO Auto-generated method stub
				super.onModifierFinished(pItem);
				resum.registerEntityModifier(new MoveXModifier(0.5f, resum.getX(), 100));
			}

		});
		
		
		
	}
	
	private void hideMenuPause(){
		
		resum.registerEntityModifier(new MoveXModifier(0.5f, resum.getX(), -200){
			@Override
			protected void onModifierFinished(IEntity pItem) {
				// TODO Auto-generated method stub
				super.onModifierFinished(pItem);
				menu_pause.registerEntityModifier(new ScaleModifier(0.5f, 1f,0){
					@Override
					protected void onModifierFinished(IEntity pItem) {
						// TODO Auto-generated method stub
						super.onModifierFinished(pItem);
						pause = false;
						left.setPosition(20, left.getY());
						right.setPosition(300, right.getY());
					}

				});
			}

		});
		
		
		menu_pause.setVisible(false);
		
		
		
	}
	
	
	private void moveSprites(int current){
		
		MoveYModifier mod_down;

		for(int i = current+1; i < ListOfCloth.size(); i++){

			/*mod_down =  new MoveYModifier(0.05f, ListOfCloth.get(i).getY(), ListOfCloth.get(i).getY()+100){
				@Override
				protected void onModifierFinished(IEntity pItem) {
					// TODO Auto-generated method stub
					super.onModifierFinished(pItem);

				}

			};
			ListOfCloth.get(i).registerEntityModifier(mod_down);*/
			ListOfCloth.get(i).setPosition(ListOfCloth.get(i).getX(), ListOfCloth.get(i).getY()+100);
			
		}
		
		//SceneManager.getInstance().getCurrentScene().sortChildren();
		
	}
	
	private void game_over(){
		 // Hide HUD
        camera.getHUD().setVisible(false);
       // camera.getHUD().setIgnoreUpdate(false);
        
		for(int i = 1; i < ListOfCloth.size(); i++){

			ListOfCloth.get(i).registerEntityModifier(new ScaleModifier(0.5f, ListOfCloth.get(i).getScaleX(), 0f));
		}
        
		moveLevelComplete(level_end);
	}
	
	private VertexBufferObjectManager vbo;
	
	private Sprite left;
	private Sprite right;
	
	private void createControllers()
	{
	    HUD yourHud = new HUD();
	    
	    final Object man = 0;
	    final Object woman = 1;
	    
		left = new Sprite(20, 650, resourcesManager.man_region, vbo)
	    {
	        public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y)
	        {

	            if (touchEvent.isActionUp() && pause == false)
	            {
	            	
	            	left.setScale(1.0f);
	            	if(count < MAX_CLOTH){
	            	  moveSprites(count);
	            	  ListOfCloth.get(count).registerEntityModifier(moveLast(true));
	            	
	            	  if(ListOfCloth.get(count).getUserData()==man){
	            		  if(combo < (MAX_COM*LONG_COM)-1) combo++;
	            		  addToScore((int)combo/LONG_COM + 1);
	            	  }else{
	            		combo = 0;
		                lifes.res_life();
		                if(lifes.getLifes()==0){
		            	  pause = true;
		  	              game_over();
		            	}
	            	  } 
	            	    
	            	}
	            	count++;
	            	if(count == MAX_CLOTH){
	            		pause = true;
	            		game_over();
	            	}
	            	
	            }
	            
	            if (touchEvent.isActionDown()){
	            	left.setScale(1.3f);
	            }
	            
	            return true;
	        };
	    };
	    
	    right = new Sprite(300, 650, resourcesManager.woman_region, vbo)
	    {
	        public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y)
	        {
	            if (touchEvent.isActionUp() && pause == false)
	            {
	            	right.setScale(1.0f);
	            	if(count < MAX_CLOTH){
	            	  moveSprites(count);
	            	  ListOfCloth.get(count).registerEntityModifier(moveLast(false));
	            	  if(ListOfCloth.get(count).getUserData()==woman){
	            		  if(combo < (MAX_COM*LONG_COM)-1) combo++;
	            		  addToScore((int)combo/LONG_COM + 1);
	            	  }else{
	            		combo = 0;
		                lifes.res_life();
		                if(lifes.getLifes()==0){
		            	  pause = true;
		  	              game_over();
		            	}
	            	  } 
	            	    
	            	}
	            	count++;
	            	if(count == MAX_CLOTH){
	            		
	            		pause = true;
	            		game_over();
	            	}
	            }
	            
	            if (touchEvent.isActionDown()){
	            	right.setScale(1.3f);
	            }
	            
	            return true;
	        };
	    };
	    
	    yourHud.registerTouchArea(left);
	    yourHud.registerTouchArea(right);
	    yourHud.attachChild(left);
	    yourHud.attachChild(right);
	    
	 // CREATE SCORE TEXT
	    scoreText = new Text(0, 0, resourcesManager.font, "Score: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
	    scoreText.setSkewCenter(0, 0);    
	    scoreText.setText("Score: 0");
	    yourHud.attachChild(scoreText);
	    
	 // CREATE RESUM BUTTON
	    resum = new Text(-200, 250, resourcesManager.font, "RESUM", new TextOptions(HorizontalAlign.LEFT), vbom){
	        public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y)
	        {
	            if (touchEvent.isActionUp())
	            {
	            	hideMenuPause();
	            	
	            }
	            
	            if (touchEvent.isActionDown()){
	            	
	            }
	            
	            return true;
	        };
	    };
	    resum.setSkewCenter(0, 0);   
	    yourHud.registerTouchArea(resum);
	    yourHud.attachChild(resum);
	    
	 // CREATE CHRONO TEXT
	    chrono = new Text(0, scoreText.getHeight()+30, resourcesManager.font, "Time: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
	    chrono.setSkewCenter(0, 0);    
	    chrono.setText("Time: 00");
	    yourHud.attachChild(chrono);
	    
	    camera.setHUD(yourHud);
	}

}

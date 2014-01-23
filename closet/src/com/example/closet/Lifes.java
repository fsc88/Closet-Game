package com.example.closet;


import org.andengine.entity.scene.Scene;

import org.andengine.entity.sprite.TiledSprite;

import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.example.closet.ResourcesManager;

public class Lifes{
	
	private TiledSprite star1;
    private TiledSprite star2;
    private TiledSprite star3;
    
    public int num_lifes;
    
    public Lifes(VertexBufferObjectManager pSpriteVertexBufferObject, Scene scene, int lifes){
    	attachStars(pSpriteVertexBufferObject,scene);
    	num_lifes = lifes;
    }
    
    public int getLifes()
    {
        return num_lifes;
    }
    
    private void attachStars(VertexBufferObjectManager pSpriteVertexBufferObject, Scene scene)
    {
    	star1 = new TiledSprite(350, 0, 50, 50, ResourcesManager.getInstance().stars_region, pSpriteVertexBufferObject);
        star2 = new TiledSprite(400, 0, 50, 50, ResourcesManager.getInstance().stars_region, pSpriteVertexBufferObject);
        star3 = new TiledSprite(450, 0, 50, 50, ResourcesManager.getInstance().stars_region, pSpriteVertexBufferObject);
    	 	
    	scene.attachChild(star1);
    	scene.attachChild(star2);
    	scene.attachChild(star3);
    }
    
    /**
     * Change star`s tile index, depends on stars count.
     * @param starsCount
     */
    public void res_life()
    {
        // Change stars tile index, based on stars count (1-3)
        switch (num_lifes)
        {
            case 1:
                star1.setCurrentTileIndex(1);
                star2.setCurrentTileIndex(1);
                star3.setCurrentTileIndex(1);
                break;
            case 2:
                star1.setCurrentTileIndex(0);
                star2.setCurrentTileIndex(1);
                star3.setCurrentTileIndex(1);
                break;
            case 3:
                star1.setCurrentTileIndex(0);
                star2.setCurrentTileIndex(0);
                star3.setCurrentTileIndex(1);
                break;
            default:
            	break;
        }
        
        num_lifes--;
        
    }

}

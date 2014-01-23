package com.example.closet;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.vbo.ISpriteVertexBufferObject;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Cloth extends Sprite{

	
	public Cloth(float pX, float pY, float pWidth, float pHeight,
			ITextureRegion pTextureRegion,
			VertexBufferObjectManager vbom) {
		super(pX, pY, pWidth, pHeight, pTextureRegion, vbom);
		// TODO Auto-generated constructor stub
	}
	
	public void setMovement(final int ipos, final int fpos)
	{
	    this.registerEntityModifier(new RotationModifier(1, ipos, fpos){
	        protected void onModifierFinished(IEntity pItem)
	        {
	                super.onModifierFinished(pItem);
	                // Your action after finishing modifier
	                setMovement(fpos,ipos);
	        }
	    });
	    
	    
	}
	
	

}

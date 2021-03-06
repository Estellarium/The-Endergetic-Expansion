package com.minecraftabnormals.endergetic.common.entities.puffbug.ai;

import java.util.EnumSet;
import java.util.Random;

import com.teamabnormals.abnormals_core.core.utils.MathUtils;
import com.teamabnormals.abnormals_core.core.utils.NetworkUtil;
import com.minecraftabnormals.endergetic.common.entities.puffbug.PuffBugEntity;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.potion.Effects;

public class PuffBugCreateItemGoal extends Goal {
	private PuffBugEntity puffbug;
	private int ticksPassed;
	private float originalHealth;
	
	public PuffBugCreateItemGoal(PuffBugEntity puffbug) {
		this.puffbug = puffbug;
		this.setMutexFlags(EnumSet.of(Flag.MOVE));
	}

	@Override
	public boolean shouldExecute() {
		return !this.puffbug.isPassenger() && this.puffbug.isInflated() && !this.puffbug.isAggressive() && this.puffbug.hasStackToCreate() && this.puffbug.isNoEndimationPlaying();
	}
	
	@Override
	public boolean shouldContinueExecuting() {
		return this.puffbug.getAttackTarget() == null && this.puffbug.isInflated() && this.puffbug.hasLevitation() && this.puffbug.hasStackToCreate() && this.puffbug.getHealth() >= this.originalHealth;
	}
	
	@Override
	public void startExecuting() {
		this.originalHealth = this.puffbug.getHealth();
	}
	
	@Override
	public void tick() {
		this.ticksPassed++;
		
		this.puffbug.getNavigator().clearPath();
		this.puffbug.setAIMoveSpeed(0.0F);
		
		if(this.ticksPassed >= 25 && this.puffbug.isNoEndimationPlaying()) {
			NetworkUtil.setPlayingAnimationMessage(this.puffbug, PuffBugEntity.MAKE_ITEM_ANIMATION);
		}
		
		this.puffbug.getRotationController().rotate(0.0F, 180.0F, 0.0F, 20);
		
		if(this.puffbug.isEndimationPlaying(PuffBugEntity.MAKE_ITEM_ANIMATION) && this.puffbug.hasStackToCreate()) {
			Random rand = this.puffbug.getRNG();
			
			if(this.puffbug.getAnimationTick() == 90) {
				ItemEntity itementity = new ItemEntity(this.puffbug.world, this.puffbug.getPosX(), this.puffbug.getPosY() - 0.5D, this.puffbug.getPosZ(), this.puffbug.getStackToCreate());
				itementity.setPickupDelay(40);
				this.puffbug.world.addEntity(itementity);
				this.puffbug.setStackToCreate(null);
				
				this.puffbug.removePotionEffect(Effects.LEVITATION);
				
				for(int i = 0; i < 6; i++) {
					double offsetX = MathUtils.makeNegativeRandomly(rand.nextFloat() * 0.1F, rand);
					double offsetZ = MathUtils.makeNegativeRandomly(rand.nextFloat() * 0.1F, rand);
				
					double x = this.puffbug.getPosX() + offsetX;
					double y = this.puffbug.getPosY() + (rand.nextFloat() * 0.05F) + 0.5F;
					double z = this.puffbug.getPosZ() + offsetZ;
					
					NetworkUtil.spawnParticle("endergetic:short_poise_bubble", x, y, z, MathUtils.makeNegativeRandomly((rand.nextFloat() * 0.15F), rand) + 0.025F, (rand.nextFloat() * 0.025F) + 0.025F, MathUtils.makeNegativeRandomly((rand.nextFloat() * 0.15F), rand) + 0.025F);
				}
			} else if(this.puffbug.getAnimationTick() == 85) {
				float pitch = this.puffbug.isChild() ? (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.5F : (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F;
				this.puffbug.playSound(this.puffbug.getItemCreationSound(), 0.1F, pitch);
			}
		}
		
		if(this.puffbug.getAnimationTick() == 90 && this.puffbug.isEndimationPlaying(PuffBugEntity.MAKE_ITEM_ANIMATION) && this.puffbug.hasStackToCreate()) {
			
		}
	}
	
	@Override
	public void resetTask() {
		this.ticksPassed = 0;
		this.originalHealth = 0.0F;
	}
}
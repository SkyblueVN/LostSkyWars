package tk.kanaostore.losteddev.skywars.nms.v1_12_R1.entity;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import net.minecraft.server.v1_12_R1.DamageSource;
import net.minecraft.server.v1_12_R1.EntityLeash;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import tk.kanaostore.losteddev.skywars.nms.BalloonEntity;

public class BalloonEntityLeash extends EntityLeash implements BalloonEntity {
  
  public BalloonEntityLeash(Location location) {
    super(((CraftWorld) location.getWorld()).getHandle());
    
    this.setPosition(location.getX(), location.getY(), location.getZ());
  }
  
  @Override
  public void kill() {
    this.dead = true;
  }
  
  @Override
  public void B_() {}
  
  @Override
  public boolean isInvulnerable(DamageSource damagesource) {
    return true;
  }

  @Override
  public void setCustomName(String s) {}

  @Override
  public void setCustomNameVisible(boolean flag) {}

  @Override
  public void die() {}

  @Override
  public boolean damageEntity(DamageSource damagesource, float f) {
    return false;
  }

  @Override
  public void setInvisible(boolean flag) {}

  public void a(NBTTagCompound nbttagcompound) {}

  public void b(NBTTagCompound nbttagcompound) {}

  public boolean c(NBTTagCompound nbttagcompound) {
    return false;
  }

  public boolean d(NBTTagCompound nbttagcompound) {
    return false;
  }

  public void e(NBTTagCompound nbttagcompound) {}

  public void f(NBTTagCompound nbttagcompound) {}
}

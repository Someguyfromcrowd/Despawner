package someguy.despawner;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Golem;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Squid;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Someguyfromcrowd
 * 
 */
public class Despawner extends JavaPlugin
{
	public void onEnable()
	{

	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		MobEnum type = MobEnum.ALL;
		if (args.length > 0)
		{
			if (args.length > 1)
			{
				if (args[args.length - 1].equalsIgnoreCase("-a"))
					type = MobEnum.NEUTRAL;
				else if (args[args.length - 1].equalsIgnoreCase("-h"))
					type = MobEnum.HOSTILE;
				else if (args[args.length - 1].equalsIgnoreCase("-n"))
					type = MobEnum.NPC;
				else
					type = MobEnum.ALL;
			}
			
			if (args.length == 1 && args[0].equalsIgnoreCase("help"))
			{
				sender.sendMessage(cmd.getUsage());
				sender.sendMessage("Flags:");
				sender.sendMessage("-a: despawn animals");
				sender.sendMessage("-h: despawn hostile mobs");
				sender.sendMessage("-n: despawn NPCs");
				return true;
			}

			if (args.length >= 1 && args.length <= 2)
			{
				if (sender instanceof Player)
				{
					Player player = (Player) sender;
					try
					{
						int radius = Integer.parseInt(args[0]);
						if (radius <= 0)
							throw new NumberFormatException();
						sender.sendMessage("Despawned " + despawnMobs(radius, player.getLocation(), type) + " mobs");
						return true;
					}
					catch (NumberFormatException e)
					{
						sender.sendMessage("Error: not a valid number");
						return false;
					}
				}
				else
				{
					sender.sendMessage("Error: Must specify a location when calling /despawn from the console");
					return false;
				}
			}
			else if (args.length >= 3)
			{
				World world = null;
				try
				{
					int radius = Integer.parseInt(args[0]);
					if (radius <= 0)
						throw new NumberFormatException();
					int x = Integer.parseInt(args[1]);
					int z = Integer.parseInt(args[2]);
					if (args.length >= 4 && !args[3].contains("-"))
					{
						world = getServer().getWorld(args[3]);		
						if (world == null)
						{
							sender.sendMessage("Invalid world");
							return false;
						}
					}
					
					if (world == null)
					{
						if (sender instanceof Player)
							world = ((Player) sender).getWorld();
						else
						{
							sender.sendMessage("Must specify a valid world to execute from the console");
							return false;
						}
					}
					sender.sendMessage("Despawned " + despawnMobs(radius, new Location(world, x, 64, z), type) + " mobs");
					
					return true;
				}
				catch (NumberFormatException e)
				{
					sender.sendMessage("Error: not a valid number");
					return false;
				}
			}
			else
				return false;
		}
		else
			return false;
	}

	private int despawnMobs(int radius, Location location, MobEnum type)
	{
		int counter = 0;
		if (type.equals(MobEnum.ALL))
		{
			for (Entity entity : location.getWorld().getEntities())
			{
				if (entity instanceof LivingEntity && !(entity instanceof HumanEntity))
				{
					if (distance(entity.getLocation(),location) <= radius)
					{
						entity.remove();
						counter++;
					}
				}
			}
		}
		else if (type.equals(MobEnum.HOSTILE))
		{
			for (Entity entity : location.getWorld().getEntities())
			{
				if (entity instanceof Monster || entity instanceof Slime || entity instanceof EnderDragon || entity instanceof Ghast)
				{
					if (distance(entity.getLocation(),location) <= radius)
					{
						entity.remove();
						counter++;
					}
				}
			}
		}
		else if (type.equals(MobEnum.NEUTRAL))
		{
			for (Entity entity : location.getWorld().getEntities())
			{
				if (entity instanceof Animals || entity instanceof Bat || entity instanceof Squid)
				{
					if (distance(entity.getLocation(),location) <= radius)
					{
						entity.remove();
						counter++;
					}
				}
			}
		}
		else if (type.equals(MobEnum.NPC))
		{
			for (Entity entity : location.getWorld().getEntities())
			{
				if (entity instanceof NPC || entity instanceof Golem)
				{
					if (distance(entity.getLocation(),location) <= radius)
					{
						entity.remove();
						counter++;
					}
				}
			}
		}
		return counter;
	}
	
	private double distance(Location arg0, Location arg1)
	{
		return Math.pow(Math.pow(arg0.getX() - arg1.getX(),2) + Math.pow(arg0.getZ() - arg1.getZ(),2),0.5);
	}
}

package rebelkeithy.mods.metallurgy.core;

import net.minecraftforge.common.Configuration;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rebelkeithy.mods.guiregistry.GuiRegistry;
import rebelkeithy.mods.metallurgy.core.metalsets.MetalSet;

@Mod(modid="Metallurgy3Core", name="Metallurgy 3 Core", version="1.4.7-1.11.13-1a")
@NetworkMod(channels = {"MetallurgyCore"}, clientSideRequired = true, serverSideRequired = false)
public class MetallurgyCore 
{
	@SidedProxy(clientSide = "rebelkeithy.mods.metallurgy.core.ClientProxy", serverSide = "rebelkeithy.mods.metallurgy.core.CommonProxy")
	public static CommonProxy proxy;
	
	@Instance(value = "Metallurgy3Core")
	public static MetallurgyCore instance;
	
	Configuration config;
	
	List<String> csvFiles;
	List<String> setsToRead;
	
	
	private static List<MetalSet> metalSets;
	
	MetalSet baseSet;
	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{		
		for(MetalSet set : getMetalSetList())
		{
			//set.initConfig();
			//set.init();
		}
		
		initConfig();
		
		for(String filename : csvFiles)
		{
			MetalInfoDatabase.readDataFromFile("/config/Metallurgy3/" + filename);
		}
		for(String set : setsToRead)
		{
			System.out.println("reading set " + set);
			MetalSet newSet = new MetalSet(set, MetalInfoDatabase.getSpreadsheetDataForSet(set));
		}
		
		NetworkRegistry.instance().registerGuiHandler(this, GuiRegistry.instance());
	}
	
	@Init
	public void init(FMLInitializationEvent event)
	{
		for(MetalSet set : getMetalSetList())
		{
			set.load();
			proxy.registerNamesForMetalSet(set);
		}
	}
	
	public void initConfig()
	{
		File fileDir = new File(MetallurgyCore.proxy.getMinecraftDir() + "/config/Metallurgy3");
    	fileDir.mkdir();
    	File cfgFile = new File(MetallurgyCore.proxy.getMinecraftDir() + "/config/Metallurgy3/MetallurgyCore.cfg");
    	
        try
        {
            cfgFile.createNewFile();
            System.out.println("[Metallurgy3] Successfully created/read configuration file for Metallurgy 3 Core");
        }
        catch (IOException e)
        {
            System.out.println("[Metallurgy3] Could not create configuration file for Metallurgy 3 Core, Reason:");
            System.out.println(e);
        }
        
		config = new Configuration(cfgFile);
		config.load();
		
		csvFiles = Arrays.asList(config.get("Metal Sets", "File List", "").value.split("\\s*,\\s*"));
		setsToRead = Arrays.asList(config.get("Metal Sets", "Metal Set List", "").value.split("\\s*,\\s*"));
		System.out.println("reading sets " + setsToRead.size());
		config.save();
	}
	
	public static List<MetalSet> getMetalSetList()
	{
		if(metalSets == null)
		{
			metalSets = new ArrayList<MetalSet>();
		}
		
		return metalSets;
	}
}

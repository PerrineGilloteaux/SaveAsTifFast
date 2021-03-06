package plugins.perrine.saveastiffast;

import plugins.adufour.ezplug.EzVarFile;
import plugins.adufour.ezplug.EzVarSequence;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import javax.swing.SwingUtilities;


import icy.common.Version;
import icy.file.Loader;
import icy.gui.frame.progress.AnnounceFrame;
import icy.gui.viewer.Viewer;
import icy.imagej.ImageJUtil;
import icy.main.Icy;
import icy.plugin.PluginLauncher;
import icy.plugin.PluginLoader;
import plugins.adufour.blocks.lang.Block;
import plugins.adufour.blocks.util.VarList;
import plugins.adufour.ezplug.EzLabel;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzVar;
import icy.sequence.Sequence;
import icy.sequence.SequenceUtil;
import ij.IJ;
import ij.ImagePlus;

public class SaveAsTifFast extends EzPlug implements Block {
	private EzVar<Sequence> inputseq=new EzVarSequence("Sequence");
	private EzVarFile outputfile=new EzVarFile("Output file", null);
	
	@Override
	protected void initialize() {
		addEzComponent(new EzLabel(getVersionString()));
	    inputseq.setToolTipText("Sequence to Save");
	    outputfile.setToolTipText("Where to save as tif");
	    if (getActiveSequence() != null)
	    	outputfile=new EzVarFile("Output file", inputseq.getValue().getFilename());
	    
		addEzComponent(inputseq);
		addEzComponent(outputfile);
	}

    @Override
    public void declareInput(VarList inputMap)
    {
        inputMap.add("inputseq", inputseq.getVariable());
        inputMap.add("outputfile", outputfile.getVariable());
    }

    @Override
    public void declareOutput(VarList outputMap)
    {
        //
    }
    
	@Override
	protected void execute(){
		// Obtain a TIFF writer
		
		ImagePlus tmpimp= ImageJUtil.convertToImageJImage(inputseq.getValue(), null);
		String mynamepath = outputfile.getValue().getPath();
		if (!mynamepath.endsWith(".tif")){
			mynamepath=mynamepath+".tif";
		}
		IJ.saveAsTiff(tmpimp,mynamepath);
		
		try {
			SwingUtilities.invokeAndWait( () -> {
				inputseq.getValue().saveXMLData();
				
			} );
		} catch (InvocationTargetException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		String inputxml=(inputseq.getValue().getOutputFilename(false)+".xml");
		String outputxml=mynamepath.replaceAll(".tif", ".xml");
		copyfile(inputxml,outputxml);
		
		if (!isHeadLess())
		    new AnnounceFrame(inputseq.getValue().getName()+" has been saved as "+ mynamepath, 5);
		
		System.out.println(inputseq.getValue().getName()+" has been saved as "+ mynamepath);
	}

	private static boolean copyfile(String inputxml, String outputxml) { 
    try { 
    	Path inputxmlPath = Paths.get(inputxml);
    	Path outputxmlPath=Paths.get(outputxml);;
    	
        Files.copy(inputxmlPath, outputxmlPath,java.nio.file.StandardCopyOption.REPLACE_EXISTING); 
        
    } catch (IOException e) { 
        e.printStackTrace(); 
        return false; 
    } 
    return true; 
}
	public static void main( final String[] args ) throws InvocationTargetException, InterruptedException
	{
		// Launch the application.
		Icy.main( args );

		// Load an image.
		final String imagePath = "samples/Cont1.lsm";
		final Sequence sequence = Loader.loadSequence( imagePath, 0, true );

		

		// Display the images.
		SwingUtilities.invokeAndWait( () -> {
			new Viewer( sequence );
			
		} );

		// Run the plugin on the last active image (the copy).
		PluginLauncher.start( PluginLoader.getPlugin( SaveAsTifFast.class.getName() ) );
	}
	@Override
	public void clean() {
		// 
	}
	
	private String getVersionString(){
		
	     
		Version version= PluginLoader.getPlugin( SaveAsTifFast.class.getName()).getVersion();
		return "version "+ version.toString();
	
	}
}

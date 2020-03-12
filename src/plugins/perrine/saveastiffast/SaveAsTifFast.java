package plugins.perrine.saveastiffast;

import plugins.adufour.ezplug.EzVarFile;
import plugins.adufour.ezplug.EzVarSequence;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import icy.gui.frame.progress.AnnounceFrame;
import icy.imagej.ImageJUtil;
import plugins.adufour.blocks.lang.Block;
import plugins.adufour.blocks.util.VarList;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzVar;
import icy.sequence.Sequence;

import ij.IJ;
import ij.ImagePlus;

public class SaveAsTifFast extends EzPlug implements Block {
	private EzVar<Sequence> inputseq=new EzVarSequence("Sequence");
	private EzVarFile outputfile=new EzVarFile("Output file", null);
	
	@Override
	protected void initialize() {
	    inputseq.setToolTipText("Sequence to Save");
	    outputfile.setToolTipText("Where to save as tif");
		
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
	protected void execute() {
		// Obtain a TIFF writer
		
		ImagePlus tmpimp= ImageJUtil.convertToImageJImage(inputseq.getValue(), null);
		String mynamepath = outputfile.getValue().getPath();
		if (!mynamepath.endsWith(".tif")){
			mynamepath=mynamepath+".tif";
		}
		IJ.saveAsTiff(tmpimp,mynamepath);
		
		
		inputseq.getValue().saveXMLData();
		
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
    	
        Files.copy(inputxmlPath, outputxmlPath); 
        
    } catch (IOException e) { 
        e.printStackTrace(); 
        return false; 
    } 
    return true; 
}

	@Override
	public void clean() {
		// 
	}
}

import java.io.*;

public class SavingLoadingFeature {
	public static boolean saveNeat(String fileName, Neat neat) {
		boolean saved = false;
		try {
			FileOutputStream saveFile = new FileOutputStream(fileName);
			ObjectOutputStream save = new ObjectOutputStream(saveFile);
			
			save.writeObject(neat);
			
			save.close();
			saved = true;
		} catch(Exception exc) {
			exc.printStackTrace();
		}
		
		return saved;
	}
	
	public static Neat loadNeat(String fileName) {
		Neat neat = null;
		try{
			FileInputStream loadFile = new FileInputStream(fileName);
			ObjectInputStream load = new ObjectInputStream(loadFile);
			
			neat = (Neat) load.readObject();
			
			load.close();
		} catch(Exception exc) {
			exc.printStackTrace();
		}
		return neat;
	}
}

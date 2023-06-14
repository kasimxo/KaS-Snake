

public class OsPaths {
	
	
	/**
	 * This method checks if the operative system is Linux or windows.
	 * This is made to change relative routes to work on both systems.
	 * If, for some reason, your system "os name" property is not Linux even when you are using a Linux distro, this will not work.
	 * @return -> <b>true</b> if the system is <b>Linux</b> based.
	 */
	public static boolean checkLinux() {
		if(System.getProperty("os.name").compareTo("Linux")==0) {
			return true;
		} else
			return false;
	}
	
	/**
	 * This method check for linux OS and if false will change any <b>' / '</b> character in a given file path so that
	 * it works for linux and windows OS. 
	 * @param path
	 * @return
	 */
	public static String cleanPath(String path) {
		if(!checkLinux()) {
			String windowsPath = "";
			for(int i= 0; i<path.length(); i++) {
				if(path.charAt(i)=='/') {
					windowsPath+="\\";
				} else {
					windowsPath+=path.charAt(i);
				}
			}
			return windowsPath;
		} else {
			return path;
			
		}
	}
	
	
	
}

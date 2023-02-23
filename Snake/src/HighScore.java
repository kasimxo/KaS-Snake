import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HighScore {
	
	/**
	 * Nos lee la mejor puntuación que tenemos en el archivo. Si no puede leerlo, da un mensaje de error.
	 * @return
	 */
	public static int readBestScore() {
		int bestScore = 0;
		
		String filename = "C:\\Users\\Andrés\\git\\KaS-Snake\\Snake\\data\\highscore.txt";
		Path filepath = Paths.get(filename);
		String bestScoreRaw = "0";
		
		try {
			bestScoreRaw = Files.readString(filepath);
			bestScore = Integer.parseInt(bestScoreRaw);
		} catch (Exception e) {
			System.err.println("El archivo highscore está dañado o no se encuentra.");
		}
		
		return bestScore;
	}
	
	/**
	 * Nos guarda la mejor puntuación.
	 * @param score
	 */
	public static void saveBestScore(int score) {
		String filename = "C:\\Users\\Andrés\\git\\KaS-Snake\\Snake\\data\\highscore.txt";
		Path filepath = Paths.get(filename);
		
		String bestScore = "" + score;
		byte[] bestScoreByte = bestScore.getBytes();
		
		try {
			Files.write(filepath, bestScoreByte);
		} catch (IOException e) {
			System.err.println("No se ha podido guardar la máxima puntuación.");
		}
	}
}

package comm;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.sikuli.script.Match;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

public class ImageSearch {
	public List<Match> findMatchesBlocking(File file, float similarity) {
	    List<Match> results = new ArrayList<>();
	    try {
	        Screen screen = new Screen();
	        Pattern target = new Pattern(file.getAbsolutePath()).similar(similarity);
	        Iterator<Match> matches = screen.findAll(target);
	        while (matches.hasNext()) results.add(matches.next());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return results;
	}
}

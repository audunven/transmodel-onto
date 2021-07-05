package gtfs;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipFile;

import com.conveyal.gtfs.GTFSFeed;

public class GTFSTest {
	
	public static void main(String[] args) {
		
		String file = "./files/rb_atb-aggregated-gtfs.zip";
		GTFSFeed feed = fromFile(file, null);
		
		Set<String> transitIds = feed.transitIds;
		
		for (String t : transitIds) {
			System.out.println("TransitId: " + t);
		}
		
		
		feed.close();
		
	}

	

	
	public static GTFSFeed fromFile(String file, String feedId) {
		  GTFSFeed feed = new GTFSFeed();
		  ZipFile zip;
		  try {
		    zip = new ZipFile(file);
		    if (feedId == null) {
		      feed.loadFromFile(zip);
		    }
		    else {
		      feed.loadFromFile(zip, feedId);
		    }
		    zip.close();
		    return feed;
		  } catch (Exception e) {
		    System.err.println(e.getMessage());
		    throw new RuntimeException(e);
		  }
		}
	


}

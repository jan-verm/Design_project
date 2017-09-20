package controllermockup;

import databaseaccess.exceptions.ClassicNotFoundException;
import interfaces.VideoControllerInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import models.Video;

public class VideoControllerMockup implements VideoControllerInterface {

	@Override
	public int addVideo(int parentId, Video video, int userId) {
		return 1;
	}

	@Override
	public Video getVideo(int videoId) throws ClassicNotFoundException {
            if (videoId == 0) {
                throw new ClassicNotFoundException();
            }
		Video v = new Video("title", "url", 999);
		v.setId(1);
		return v;
	}

	@Override
	public List<Video> getAllVideos(int parentId) {
		List<Video> list = new ArrayList<>();
            try {
                list.add(getVideo(1));
            } catch (ClassicNotFoundException ex) {
                Logger.getLogger(VideoControllerMockup.class.getName()).log(Level.SEVERE, null, ex);
            }
		return list;
	}

	@Override
	public void updateVideo(int videoId, Video video, int userId) throws ClassicNotFoundException {
            if (videoId == 0) {
                throw new ClassicNotFoundException();
            }
		return;
	}

	@Override
	public void deleteVideo(int videoId, int userId) throws ClassicNotFoundException {
            if (videoId == 0) {
                throw new ClassicNotFoundException();
            }
		return;
	}

}

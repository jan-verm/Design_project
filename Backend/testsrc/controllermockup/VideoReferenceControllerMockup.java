package controllermockup;

import interfaces.VideoReferenceControllerInterface;

import java.util.ArrayList;
import java.util.List;

import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import models.Video;
import models.VideoReference;


public class VideoReferenceControllerMockup implements VideoReferenceControllerInterface{

	@Override
	public int addVideoReference(int videoId, int commentId,
			VideoReference videoRef, int userId) throws ClassicNotFoundException {
            if (videoId == 0 || commentId == 0) {
                throw new ClassicNotFoundException();
            }
		return 1;
	}

	@Override
	public VideoReference getVideoReference(int refId) throws ClassicNotFoundException {
            if (refId == 0) {
                throw new ClassicNotFoundException();
            }
		Video video = new Video("title", "url", 999);
		video.setId(1);
		return new VideoReference(video, 12);
	}

	@Override
	public List<VideoReference> getVideoReferences(int commentId) throws ClassicNotFoundException {
            if (commentId == 0) {
                throw new ClassicNotFoundException();
            }
		List<VideoReference> list = new ArrayList<>();
		Video video = new Video("title", "url", 999);
		video.setId(1);
		list.add(new VideoReference(video, 12));
		return list;
	}

	@Override
	public void updateVideoReference(int videoId, int commentId,
			VideoReference videoRef, int userId) throws ClassicNotFoundException {
            if (videoId == 0 || commentId == 0) {
                throw new ClassicNotFoundException();
            }
		return;
	}

	@Override
	public void deleteVideoReference(int videoId, int refId, int userId) throws ClassicNotFoundException {
            if (videoId == 0 || refId == 0) {
                throw new ClassicNotFoundException();
            }
		return;
	}

	@Override
	public List<VideoReference> getSelfVideoReferences(int videoId,
			int commentId) throws ClassicDatabaseException,
			ClassicNotFoundException {
            if (videoId == 0 || commentId == 0) {
                throw new ClassicNotFoundException();
            }
		List<VideoReference> list = new ArrayList<>();
		Video video = new Video("title", "url", 999);
		video.setId(1);
		list.add(new VideoReference(video, 12));
		return list;
	}

    @Override
    public int[] getParents(int courseNotesID) throws ClassicDatabaseException, ClassicNotFoundException {
        return new int[]{1,0};
    }

}

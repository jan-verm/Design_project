/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import interfaces.CourseNotesControllerInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import models.Comment;
import models.CourseNotes;
import models.CourseNotesReference;
import models.Location;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfAnnotation;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import courses.Role;
import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.exceptions.ClassicUnauthorizedException;
import databaseaccess.interfaces.ICourseDao;
import databaseaccess.interfaces.ICourseNotesDao;
import databaseaccess.interfaces.IUserDao;

/**
 *
 * @author Juta
 */
public class CourseNotesController implements CourseNotesControllerInterface {

    private final ICourseNotesDao dao;
    private final ICourseDao courseDao;
    private final IUserDao userdao;
    private final String prefix;

    /**
     * create a course notes controller
     * @param dao class that interacts with the db for course notes
     * @param courseDao class that interacts with the db for courses
     * @param userdao class that interacts with the db for users
     * @param prefix path where the course notes will be uploaded
     */
    public CourseNotesController(ICourseNotesDao dao, ICourseDao courseDao, IUserDao userdao, String prefix) {
        this.dao = dao;
        this.courseDao = courseDao;
        this.userdao = userdao;
        this.prefix = prefix;
    }

    private User getUser(int userId) throws ClassicDatabaseException, ClassicNotFoundException {
        return userdao.getUser(userId);
    }

    private void checkOwnershipCourse(int userId, int courseId) throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        User user = getUser(userId);
        User owner = courseDao.getOwner(courseId);
        if (user.getRole() != Role.ADMIN && user.getId() != owner.getId()) {
            throw new ClassicUnauthorizedException("user with id " + userId + " is not allowed to perform this action");
        }
    }

    private void checkOwnershipCourseNotes(int userId, int courseNotesId) throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        User user = getUser(userId);
        User owner = dao.getOwner(courseNotesId);
        if (user.getRole() != Role.ADMIN && user.getId() != owner.getId()) {
            throw new ClassicUnauthorizedException("user with id " + userId + " is not allowed to perform this action");
        }
    }

    @Override
    public int addCourseNotes(int parentId, CourseNotes courseNotes, int userId) throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        checkOwnershipCourse(userId, parentId);
        return dao.addCourseNotes(parentId, courseNotes);
    }

    @Override
    public CourseNotes getCourseNotes(int courseNotesId) throws ClassicDatabaseException, ClassicNotFoundException {
        return dao.getCourseNotes(courseNotesId);
    }

    @Override
    public List<CourseNotes> getAllCourseNotes(int parentId) throws ClassicDatabaseException, ClassicNotFoundException {
        return dao.getParentCourseNotes(parentId);
    }

    @Override
    public void updateCourseNotes(int courseNotesId, CourseNotes courseNotes, int userId) throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        checkOwnershipCourseNotes(userId, courseNotesId);
        courseNotes.setId(courseNotesId);
        dao.updateCourseNotes(courseNotes);
    }

    @Override
    public void deleteCourseNotes(int courseNotesId, int userId) throws ClassicDatabaseException, ClassicNotFoundException {
        checkOwnershipCourseNotes(userId, courseNotesId);

        // First delete from filesystem, then from db
        CourseNotes cn = dao.getCourseNotes(courseNotesId);
        String url = cn.getUrl();
        // Parse pdfName out of the url in the db
        String pdfName = url.substring(url.lastIndexOf('/') + 1, url.length());
        // concat the file system prefix to the pdfName
        File cnf = new File(prefix + "/" + pdfName);
        // finally delete
        if (cnf.exists()) {
            cnf.delete();
        }

        dao.deleteCourseNotes(courseNotesId);

    }

    @Override
    public String addAllPdfComments(CourseNotes courseNotes, List<Comment> comments, boolean annotations, boolean questions) {
        String outputFile = "";
        try {
            //generate outputFile name: oldfilename_timestamp.pdf
            String inputFile = courseNotes.getUrl();
            if (inputFile.indexOf(".") > 0) {
                outputFile = inputFile.substring(inputFile.lastIndexOf("/") + 1, inputFile.lastIndexOf("."));
            }
            outputFile += "_" + System.currentTimeMillis() + ".pdf";

            //make the new pdf file to annotate on:
            // Save file to a temporary location. This file should be deleted by the caller of this function.
            Document document = new Document();
            PdfCopy copy = new PdfCopy(document, new FileOutputStream(outputFile));
            document.open();
            PdfReader reader = new PdfReader(inputFile);
            copy.addDocument(reader);
            document.close();
            reader.close();
            if (!comments.isEmpty()) {
                for (Comment comment : comments) {
                    //check if it is an allowed kind of comment (annotation or question)
                    if ((!comment.isQuestion() && annotations) || (comment.isQuestion() && questions)) {
                        for (CourseNotesReference cnref : comment.getSelfCourseNotesReferences()) {
                            addPdfComment(outputFile, comment, cnref);
                        }
                    }
                }
            }
        } catch (DocumentException | IOException ex) {
            Logger.getLogger(CourseNotesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return outputFile;
    }

    @Override
    public void addPdfComment(String outputFile, Comment comment, CourseNotesReference cnref) {
        FileOutputStream fos = null;
        try {
            //bottomleft corner of pdf is (0,0)
            //annotation gets added on the left top corner of the rectangle, only llx and ury matter.
            // Only take 1st selfref now
            Location loc = cnref.getLocations().get(0);
            PdfReader reader = new PdfReader(outputFile);
            Rectangle mediabox = reader.getPageSize(loc.getPagenumber());
            int pdfWidth = (int) mediabox.getWidth();
            //set llx to somewhere in the left or right margin.
            float llx;
            if (loc.getX1() < pdfWidth / 2) {
                llx = (float) (pdfWidth * 0.06);
            } else {
                llx = (float) (pdfWidth - (pdfWidth * 0.06));
            }
            Rectangle rect = new Rectangle(llx, (float) loc.getY1(), (float) loc.getX2(), (float) loc.getY2() + 15);

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            PdfStamper stamper = new PdfStamper(reader, byteStream);
            PdfWriter writer = stamper.getWriter();

            //Make an icon based on the type of comment
            String iconType = comment.isQuestion() ? "Help" : "Note";
            PdfAnnotation annotation = PdfAnnotation.createText(
                    writer, rect, comment.getUser().getUsername(), comment.getBody(), true, iconType);
            stamper.addAnnotation(annotation, loc.getPagenumber());
            stamper.close();
            reader.close();
            //write byteStream content to the input file
            fos = new FileOutputStream(outputFile);
            fos.write(byteStream.toByteArray());

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CourseNotesController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(CourseNotesController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(CourseNotesController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}

package com.application.cortesluis.housekeeper.data;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by luis_cortes on 7/2/17.
 */

public class AnnouncementBank {
    private static AnnouncementBank announcementBank;

    private List<Announcement> announcements;

    public static AnnouncementBank get(Context context) {
        if (announcementBank == null) {
            announcementBank = new AnnouncementBank(context);
        }
        return announcementBank;
    }

    private AnnouncementBank(Context context) {
        announcements = new ArrayList<>();

//        for (int i=0; i < 41; i++) {
//            User user;
//
//            if (i % 2 == 0)
//                user = new User("Luis Cortes");
//            else
//                user = new User("Karly Chavez");
//
//            String msg = "We're so excited for this weekend's concert -- Mozart, live in, person! October 12, 13, 14, 15pm ony $15!";
//            Announcement announcement = new Announcement(msg, user, "Jan 3");
//            announcements.add(announcement);
//        }
    }

    public void addAnnouncement(Announcement a) {
        announcements.add(a);
    }

    public List<Announcement> getAnnouncements() {
        return this.announcements;
    }

    public Announcement getAnnouncement(String uid) {
        for (Announcement announcement : announcements) {
            if (announcement.getAuthorId().equals(uid)) {
                return announcement;
            }
        }
        return null;
    }
}

/* ISSUE #3022 Provide setting for admin to decide if a new consent document version should 
trigger a consent flow in the mobile app for enrolled participants */
alter table fda_hphc.consent add enroll_again bit(1) DEFAULT NULL;
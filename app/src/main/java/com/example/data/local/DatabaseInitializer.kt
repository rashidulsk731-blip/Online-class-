package com.example.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseInitializer {

    suspend fun populateInitialData(db: AppDatabase) = withContext(Dispatchers.IO) {
        val userDao = db.userDao()
        val schoolDao = db.schoolDao()
        val subjectDao = db.subjectDao()
        val courseDao = db.courseDao()
        val learningDao = db.learningDao()
        val examDao = db.examDao()
        val resultDao = db.resultDao()
        val attendanceDao = db.attendanceDao()
        val commDao = db.communicationDao()

        // 1. School Information
        schoolDao.insertOrUpdateSchool(
            SchoolEntity(
                id = "school_1",
                name = "PM Shri Berbhngi HS School",
                adminName = "Rashidul Sk / Principal Desk",
                contactNumber = "9387628351",
                email = "rashidul728@gmail.com",
                address = "Berbhngi, Assam - 783383"
            )
        )

        // 2. Users & Roles
        // Default Student
        userDao.insertUser(
            UserEntity(
                id = "student_1",
                email = "student@school.edu",
                phone = "9876543210",
                passwordHash = "student123",
                role = "STUDENT",
                name = "Manash Jyoti Nath"
            )
        )
        userDao.insertStudent(
            StudentEntity(
                id = "student_1",
                userId = "student_1",
                rollNumber = "101",
                classGrade = "Class 12",
                schoolName = "PM Shri Berbhngi HS School",
                parentName = "Biren Nath",
                phone = "9876543210",
                email = "student@school.edu"
            )
        )

        // Default Teacher
        userDao.insertUser(
            UserEntity(
                id = "teacher_1",
                email = "teacher@school.edu",
                phone = "9854012345",
                passwordHash = "teacher123",
                role = "TEACHER",
                name = "Pranjal Sharma"
            )
        )
        userDao.insertTeacher(
            TeacherEntity(
                id = "teacher_1",
                userId = "teacher_1",
                name = "Pranjal Sharma",
                designation = "Senior Post Graduate Teacher",
                subjectsTaught = "Assamese, Education, History",
                phone = "9854012345",
                email = "teacher@school.edu"
            )
        )

        // Default Admin
        userDao.insertUser(
            UserEntity(
                id = "admin_1",
                email = "admin@school.edu",
                phone = "9387628351",
                passwordHash = "admin123",
                role = "ADMIN",
                name = "School Administrator"
            )
        )
        userDao.insertAdmin(
            AdminEntity(
                id = "admin_1",
                userId = "admin_1",
                name = "School Administrator",
                email = "admin@school.edu"
            )
        )

        // 3. The 10 Specific Subjects
        val subjects = listOf(
            SubjectEntity(
                id = "sub_assamese",
                nameEn = "Assamese",
                nameAs = "অসমীয়া",
                code = "ASM-101",
                iconName = "Book",
                description = "Assamese literature, grammar, essay writing, and cultural heritage.",
                classGrade = "Class 12",
                teacherName = "Pranjal Sharma"
            ),
            SubjectEntity(
                id = "sub_english",
                nameEn = "English",
                nameAs = "ইংৰাজী",
                code = "ENG-102",
                iconName = "Language",
                description = "English prose, poetry, advanced reading comprehension and writing skills.",
                classGrade = "Class 12",
                teacherName = "M. H. Barbhuiya"
            ),
            SubjectEntity(
                id = "sub_education",
                nameEn = "Education",
                nameAs = "শিক্ষা",
                code = "EDU-103",
                iconName = "School",
                description = "Principles of educational psychology, modern pedagogy and child development.",
                classGrade = "Class 12",
                teacherName = "Pranjal Sharma"
            ),
            SubjectEntity(
                id = "sub_iti",
                nameEn = "ITI",
                nameAs = "আই টি আই",
                code = "ITI-104",
                iconName = "Build",
                description = "Technical skills, industrial electronics, computer hardware and vocational training.",
                classGrade = "Class 12",
                teacherName = "Anupam Das"
            ),
            SubjectEntity(
                id = "sub_pol_science",
                nameEn = "Political Science",
                nameAs = "ৰাজনীতি বিজ্ঞান",
                code = "POL-105",
                iconName = "Gavel",
                description = "Indian constitution, international relations, democratic governance and politics.",
                classGrade = "Class 12",
                teacherName = "Dr. Hiren Kalita"
            ),
            SubjectEntity(
                id = "sub_healthcare",
                nameEn = "Healthcare",
                nameAs = "স্বাস্থ্যসেৱা",
                code = "HLT-106",
                iconName = "LocalHospital",
                description = "Basic human anatomy, first aid, public health sanitation, and patient care.",
                classGrade = "Class 12",
                teacherName = "Sister Nirmala Roy"
            ),
            SubjectEntity(
                id = "sub_sociology",
                nameEn = "Sociology",
                nameAs = "সমাজতত্ত্ব",
                code = "SOC-107",
                iconName = "Groups",
                description = "Social structures, tribal communities of North-East India and sociological theories.",
                classGrade = "Class 12",
                teacherName = "Dr. Hiren Kalita"
            ),
            SubjectEntity(
                id = "sub_bihu",
                nameEn = "Bihu",
                nameAs = "বিহু",
                code = "BIH-108",
                iconName = "MusicNote",
                description = "Assamese folk culture, Rongali, Kongali and Bhogali Bihu rhythms, songs and instruments.",
                classGrade = "Class 12",
                teacherName = "Rupjyoti Saikia"
            ),
            SubjectEntity(
                id = "sub_history",
                nameEn = "History",
                nameAs = "ইতিহাস",
                code = "HIS-109",
                iconName = "AccountBalance",
                description = "Ahom kingdom chronicle, Indian national movement, and modern world history.",
                classGrade = "Class 12",
                teacherName = "Pranjal Sharma"
            ),
            SubjectEntity(
                id = "sub_dance",
                nameEn = "Dance",
                nameAs = "নৃত্য",
                code = "DAN-110",
                iconName = "Celebration",
                description = "Sattriya classical dance traditions, folk expressions, mudras and expressions.",
                classGrade = "Class 12",
                teacherName = "Rupjyoti Saikia"
            )
        )
        for (sub in subjects) {
            subjectDao.insertSubject(sub)
        }

        // 4. Paid Courses (₹100)
        val courses = listOf(
            CourseEntity(
                id = "course_1",
                title = "Complete HS 2nd Year Assamese Mastery",
                titleAs = "দ্বাদশ শ্ৰেণীৰ সম্পূৰ্ণ অসমীয়া পাঠ্যক্ৰম",
                subjectId = "sub_assamese",
                classGrade = "Class 12",
                teacher = "Pranjal Sharma",
                description = "Complete syllabus coverage with grammar formulas, critical essays, and previous 10 years question solutions.",
                price = 100.0,
                durationHours = 32,
                totalLessons = 26
            ),
            CourseEntity(
                id = "course_2",
                title = "Political Science & Indian Constitution",
                titleAs = "ৰাজনীতি বিজ্ঞান আৰু ভাৰতীয় সংবিধান",
                subjectId = "sub_pol_science",
                classGrade = "Class 12",
                teacher = "Dr. Hiren Kalita",
                description = "In-depth video analysis of Indian Constitution, Parliament, Judiciary, and World Politics.",
                price = 100.0,
                durationHours = 28,
                totalLessons = 22
            ),
            CourseEntity(
                id = "course_3",
                title = "Vocational ITI Computer & Hardware Skills",
                titleAs = "আই টি আই কাৰিকৰী আৰু কম্পিউটাৰ দক্ষতা",
                subjectId = "sub_iti",
                classGrade = "Class 12",
                teacher = "Anupam Das",
                description = "Practical guide to computer assembly, troubleshooting, networking, and modern workplace tools.",
                price = 100.0,
                durationHours = 24,
                totalLessons = 18
            ),
            CourseEntity(
                id = "course_4",
                title = "Assam Heritage: Bihu & Sattriya Traditions",
                titleAs = "অসমৰ সংস্কৃতি: বিহু আৰু সত্ৰীয়া পৰম্পৰা",
                subjectId = "sub_bihu",
                classGrade = "Class 12",
                teacher = "Rupjyoti Saikia",
                description = "Master authentic Bihu geet, Dhol rhythms, and Sattriya performance traditions with step-by-step videos.",
                price = 100.0,
                durationHours = 20,
                totalLessons = 15
            )
        )
        for (course in courses) {
            courseDao.insertCourse(course)
        }

        // Pre-enroll student in course_1
        courseDao.insertEnrollment(
            EnrollmentEntity(
                id = "enr_1",
                studentId = "student_1",
                courseId = "course_1",
                progressPercent = 45,
                status = "ACTIVE"
            )
        )

        // 5. Video Classes
        val videos = listOf(
            VideoEntity(
                id = "vid_1",
                subjectId = "sub_assamese",
                classGrade = "Class 12",
                chapter = "Chapter 1: Morom (মৰম)",
                title = "Poetry Analysis and Word Meanings",
                duration = "24:15 min",
                videoUrl = "https://www.youtube.com/watch?v=sample_asm_1",
                teacher = "Pranjal Sharma",
                orderIndex = 1,
                isCompleted = true
            ),
            VideoEntity(
                id = "vid_2",
                subjectId = "sub_assamese",
                classGrade = "Class 12",
                chapter = "Chapter 2: Byakaran (ব্যাকৰণ)",
                title = "Sandhi and Pratyay Rules Explained",
                duration = "31:40 min",
                videoUrl = "https://www.youtube.com/watch?v=sample_asm_2",
                teacher = "Pranjal Sharma",
                orderIndex = 2,
                isCompleted = false
            ),
            VideoEntity(
                id = "vid_3",
                subjectId = "sub_english",
                classGrade = "Class 12",
                chapter = "Chapter 1: The Last Lesson",
                title = "Theme, Character Sketch & Board Q&A",
                duration = "28:50 min",
                videoUrl = "https://www.youtube.com/watch?v=sample_eng_1",
                teacher = "M. H. Barbhuiya",
                orderIndex = 1,
                isCompleted = false
            ),
            VideoEntity(
                id = "vid_4",
                subjectId = "sub_pol_science",
                classGrade = "Class 12",
                chapter = "Chapter 1: Cold War Era",
                title = "Origins, Superpowers & Non-Aligned Movement",
                duration = "35:10 min",
                videoUrl = "https://www.youtube.com/watch?v=sample_pol_1",
                teacher = "Dr. Hiren Kalita",
                orderIndex = 1,
                isCompleted = false
            ),
            VideoEntity(
                id = "vid_5",
                subjectId = "sub_healthcare",
                classGrade = "Class 12",
                chapter = "Chapter 1: Vital Signs & CPR",
                title = "Hands-on First Aid & Emergency Response",
                duration = "22:05 min",
                videoUrl = "https://www.youtube.com/watch?v=sample_hlt_1",
                teacher = "Sister Nirmala Roy",
                orderIndex = 1,
                isCompleted = false
            ),
            VideoEntity(
                id = "vid_6",
                subjectId = "sub_bihu",
                classGrade = "Class 12",
                chapter = "Chapter 1: Dhol Taals",
                title = "Basic Bihu Dhol Rhythms & Peepa accompaniment",
                duration = "19:45 min",
                videoUrl = "https://www.youtube.com/watch?v=sample_bih_1",
                teacher = "Rupjyoti Saikia",
                orderIndex = 1,
                isCompleted = false
            )
        )
        for (v in videos) {
            learningDao.insertVideo(v)
        }

        // 6. Live Classes
        val liveClasses = listOf(
            LiveClassEntity(
                id = "live_1",
                title = "Assamese Grammar Live Doubt Session",
                subjectId = "sub_assamese",
                classGrade = "Class 12",
                teacher = "Pranjal Sharma",
                scheduledDate = "Tomorrow, 10:30 AM",
                scheduledTime = "10:30 AM - 11:30 AM",
                joinUrl = "https://meet.google.com/pms-hs-asm",
                status = "UPCOMING"
            ),
            LiveClassEntity(
                id = "live_2",
                title = "Political Science: Mock Board Prep Live",
                subjectId = "sub_pol_science",
                classGrade = "Class 12",
                teacher = "Dr. Hiren Kalita",
                scheduledDate = "Friday, 02:00 PM",
                scheduledTime = "02:00 PM - 03:00 PM",
                joinUrl = "https://meet.google.com/pms-hs-pol",
                status = "UPCOMING"
            ),
            LiveClassEntity(
                id = "live_3",
                title = "ITI Hardware Troubleshooting Q&A",
                subjectId = "sub_iti",
                classGrade = "Class 12",
                teacher = "Anupam Das",
                scheduledDate = "Yesterday, 11:00 AM",
                scheduledTime = "11:00 AM - 12:00 PM",
                joinUrl = "https://meet.google.com/pms-hs-iti",
                status = "COMPLETED"
            )
        )
        for (lc in liveClasses) {
            learningDao.insertLiveClass(lc)
        }

        // 7. Study Materials & PDFs (Digital Library)
        val materials = listOf(
            StudyMaterialEntity(
                id = "mat_1",
                title = "Assamese Complete Chapter Notes & Question Bank",
                subjectId = "sub_assamese",
                classGrade = "Class 12",
                chapter = "All Chapters",
                category = "Notes",
                fileUrl = "https://example.com/materials/assamese_class12_notes.pdf",
                fileSize = "3.2 MB",
                pageCount = 28,
                date = "2026-09-18",
                contentPreview = "অসমীয়া পাঠ্যপুথিৰ গুৰুত্বপূৰ্ণ প্ৰশ্নোত্তৰ, ব্যাখ্যান আৰু ব্যাকৰণ নিয়মসমূহ বিতংকৈ উল্লেখ কৰা হৈছে।"
            ),
            StudyMaterialEntity(
                id = "mat_2",
                title = "English Core Model Question Paper 2026",
                subjectId = "sub_english",
                classGrade = "Class 12",
                chapter = "Full Syllabus",
                category = "Question Paper",
                fileUrl = "https://example.com/materials/english_model_paper.pdf",
                fileSize = "1.8 MB",
                pageCount = 8,
                date = "2026-09-20",
                contentPreview = "Sample question paper based on latest AHSEC / Board guidelines with marking scheme."
            ),
            StudyMaterialEntity(
                id = "mat_3",
                title = "Education Psychology & Learning Theories PDF",
                subjectId = "sub_education",
                classGrade = "Class 12",
                chapter = "Unit 2: Learning Psychology",
                category = "Study Material",
                fileUrl = "https://example.com/materials/education_psychology.pdf",
                fileSize = "4.5 MB",
                pageCount = 36,
                date = "2026-09-15",
                contentPreview = "Thorndike's laws of learning, Pavlovian conditioning and Skinner's operant conditioning formulas."
            ),
            StudyMaterialEntity(
                id = "mat_4",
                title = "ITI Electric Circuit & Logic Gates Practice Sheet",
                subjectId = "sub_iti",
                classGrade = "Class 12",
                chapter = "Unit 4: Digital Electronics",
                category = "Practice Material",
                fileUrl = "https://example.com/materials/iti_practice_gates.pdf",
                fileSize = "2.1 MB",
                pageCount = 14,
                date = "2026-09-21",
                contentPreview = "Practice diagrams, Boolean expressions, breadboard wiring schematics and lab assignments."
            )
        )
        for (m in materials) {
            learningDao.insertStudyMaterial(m)
        }

        // 8. Online Exams & Questions
        val exam1 = ExamEntity(
            id = "exam_1",
            title = "Assamese Mid-Term Test: Poetry & Grammar",
            subjectId = "sub_assamese",
            classGrade = "Class 12",
            totalMarks = 20,
            durationMinutes = 20,
            examDate = "Ongoing / Live",
            instructions = "Read each question carefully. Each correct answer carries 5 marks. No negative marks."
        )
        examDao.insertExam(exam1)

        val questions1 = listOf(
            QuestionEntity(
                id = "q_1",
                examId = "exam_1",
                questionNumber = 1,
                questionText = "'মৰম' কবিতাটিৰ মূল ভাববস্তু কি?",
                optionA = "দেশপ্ৰেম",
                optionB = "প্ৰকৃতি আৰু মানৱীয় সহমৰ্মিতা",
                optionC = "বিজ্ঞান চৰ্চা",
                optionD = "ভ্ৰমণ কাহিনী",
                correctOption = 1,
                marks = 5,
                explanation = "'মৰম' কবিতাত কবিয়ে প্ৰকৃতি আৰু মানৱ হৃদয়ৰ গভীৰ মৰম প্ৰকাশ কৰিছে।"
            ),
            QuestionEntity(
                id = "q_2",
                examId = "exam_1",
                questionNumber = 2,
                questionText = "তলৰ কোনটো শব্দৰ সন্ধি সঠিক: বিদ্যা + আলয়?",
                optionA = "বিদ্যাকুল",
                optionB = "বিদ্যালয়",
                optionC = "বিদ্যাময়",
                optionD = "বিদ্যাগাৰ",
                correctOption = 1,
                marks = 5,
                explanation = "অ + আ বা আ + আ মিলি আ-কাৰ হয়, গতিকে বিদ্যালয়।"
            ),
            QuestionEntity(
                id = "q_3",
                examId = "exam_1",
                questionNumber = 3,
                questionText = "ৰঙালী বিহুৰ প্ৰথম দিনটোক কি বোলা হয়?",
                optionA = "মানুহ বিহু",
                optionB = "গৰু বিহু",
                optionC = "গোঁসাই বিহু",
                optionD = "চেৰা বিহু",
                correctOption = 1,
                marks = 5,
                explanation = "ৰঙালী বিহুৰ প্ৰথম দিনটো গৰুৰ সন্মানত গৰু বিহু হিচাপে পালন কৰা হয়।"
            ),
            QuestionEntity(
                id = "q_4",
                examId = "exam_1",
                questionNumber = 4,
                questionText = "সত্ৰীয়া নৃত্যক কেতিয়া ভাৰত চৰকাৰে শাস্ত্ৰীয় নৃত্যৰ স্বীকৃতি প্ৰদান কৰে?",
                optionA = "১৯৯০",
                optionB = "২০০০",
                optionC = "২০১০",
                optionD = "২০২০",
                correctOption = 1,
                marks = 5,
                explanation = "২০০০ চনৰ ১৫ নৱেম্বৰত সঙ্গীত নাটক একাডেমিয়ে সত্ৰীয়া নৃত্যক শাস্ত্ৰীয় স্বীকৃতি দিয়ে।"
            )
        )
        examDao.insertQuestions(questions1)

        // 9. Results
        val results = listOf(
            ResultEntity(
                id = "res_1",
                studentId = "student_1",
                studentName = "Manash Jyoti Nath",
                classGrade = "Class 12",
                rollNumber = "101",
                subjectId = "sub_assamese",
                subjectName = "Assamese",
                examName = "Quarterly Unit Test 1",
                totalMarks = 50,
                obtainedMarks = 44,
                percentage = 88.0,
                grade = "A+",
                date = "2026-08-25"
            ),
            ResultEntity(
                id = "res_2",
                studentId = "student_1",
                studentName = "Manash Jyoti Nath",
                classGrade = "Class 12",
                rollNumber = "101",
                subjectId = "sub_pol_science",
                subjectName = "Political Science",
                examName = "Monthly Evaluation Test",
                totalMarks = 50,
                obtainedMarks = 41,
                percentage = 82.0,
                grade = "A",
                date = "2026-09-02"
            ),
            ResultEntity(
                id = "res_3",
                studentId = "student_1",
                studentName = "Manash Jyoti Nath",
                classGrade = "Class 12",
                rollNumber = "101",
                subjectId = "sub_english",
                subjectName = "English",
                examName = "Reading & Writing Assessment",
                totalMarks = 40,
                obtainedMarks = 36,
                percentage = 90.0,
                grade = "A+",
                date = "2026-09-12"
            )
        )
        for (r in results) {
            resultDao.insertResult(r)
        }

        // 10. Attendance
        val attendanceRecords = listOf(
            AttendanceEntity(
                id = "att_1",
                studentId = "student_1",
                studentName = "Manash Jyoti Nath",
                classGrade = "Class 12",
                date = "2026-09-22",
                isPresent = true,
                remarks = "Attended All Periods"
            ),
            AttendanceEntity(
                id = "att_2",
                studentId = "student_1",
                studentName = "Manash Jyoti Nath",
                classGrade = "Class 12",
                date = "2026-09-21",
                isPresent = true,
                remarks = "Attended All Periods"
            ),
            AttendanceEntity(
                id = "att_3",
                studentId = "student_1",
                studentName = "Manash Jyoti Nath",
                classGrade = "Class 12",
                date = "2026-09-19",
                isPresent = true,
                remarks = "Attended Morning Sessions"
            ),
            AttendanceEntity(
                id = "att_4",
                studentId = "student_1",
                studentName = "Manash Jyoti Nath",
                classGrade = "Class 12",
                date = "2026-09-18",
                isPresent = false,
                remarks = "Medical Leave"
            ),
            AttendanceEntity(
                id = "att_5",
                studentId = "student_1",
                studentName = "Manash Jyoti Nath",
                classGrade = "Class 12",
                date = "2026-09-17",
                isPresent = true,
                remarks = "Attended All Periods"
            )
        )
        attendanceDao.insertAttendanceList(attendanceRecords)

        // 11. Doubts
        val doubts = listOf(
            DoubtEntity(
                id = "doubt_1",
                studentId = "student_1",
                studentName = "Manash Jyoti Nath",
                subjectId = "sub_assamese",
                chapter = "Chapter 2: Byakaran",
                question = "সন্ধি আৰু সমাসৰ মাজত প্ৰধান পাৰ্থক্য কি?",
                imageUrl = "",
                status = "ANSWERED",
                reply = "সন্ধি হৈছে দুটা ধ্বনি বা বৰ্ণৰ মিলন, আনহাতে সমাস হৈছে অৰ্থৰ সংগতি থকা দুটা বা ততোধিক পদৰ একপদিকৰণ।",
                repliedBy = "Pranjal Sharma (Teacher)",
                date = "2026-09-20"
            ),
            DoubtEntity(
                id = "doubt_2",
                studentId = "student_1",
                studentName = "Manash Jyoti Nath",
                subjectId = "sub_pol_science",
                chapter = "Chapter 1: Cold War",
                question = "Please explain the Cuban Missile Crisis timeline simply.",
                imageUrl = "",
                status = "PENDING",
                reply = "",
                repliedBy = "",
                date = "2026-09-22"
            )
        )
        for (d in doubts) {
            commDao.insertDoubt(d)
        }

        // 12. Messages
        val messages = listOf(
            MessageEntity(
                id = "msg_1",
                conversationId = "conv_student_teacher",
                senderId = "student_1",
                senderName = "Manash Jyoti Nath",
                senderRole = "STUDENT",
                receiverId = "teacher_1",
                content = "Sir, will tomorrow's Assamese grammar live class be recorded?",
                timestamp = System.currentTimeMillis() - 86400000,
                isRead = true
            ),
            MessageEntity(
                id = "msg_2",
                conversationId = "conv_student_teacher",
                senderId = "teacher_1",
                senderName = "Pranjal Sharma (Teacher)",
                senderRole = "TEACHER",
                receiverId = "student_1",
                content = "Yes Manash, the complete session will be uploaded under Video Classes within 2 hours.",
                timestamp = System.currentTimeMillis() - 43200000,
                isRead = true
            )
        )
        for (m in messages) {
            commDao.insertMessage(m)
        }

        // 13. Notifications
        val notifications = listOf(
            NotificationEntity(
                id = "notif_1",
                recipientId = "",
                title = "New Video Lesson Uploaded",
                titleAs = "নতুন ভিডিঅ' পাঠ আপলোড কৰা হৈছে",
                message = "Watch 'Sandhi & Pratyay Rules' under Assamese subject.",
                messageAs = "অসমীয়া বিষয়ৰ 'সন্ধি আৰু প্ৰত্যয় নিয়ম' এতিয়া উপলব্ধ।",
                type = "VIDEO",
                timestamp = System.currentTimeMillis() - 7200000,
                isRead = false
            ),
            NotificationEntity(
                id = "notif_2",
                recipientId = "",
                title = "Live Class Scheduled for Tomorrow",
                titleAs = "কাইলৈৰ লাইভ ক্লাছ নিৰ্ধাৰণ কৰা হ'ল",
                message = "Assamese Grammar Live Class at 10:30 AM by Pranjal Sharma.",
                messageAs = "প্ৰাঞ্জল শৰ্মা চাৰৰ অসমীয়া ব্যাকৰণ লাইভ ক্লাছ পুৱা ১০:৩০ বজাত।",
                type = "LIVE_CLASS",
                timestamp = System.currentTimeMillis() - 18000000,
                isRead = false
            ),
            NotificationEntity(
                id = "notif_3",
                recipientId = "",
                title = "PM Shri Berbhngi School Notice",
                titleAs = "পি এম শ্ৰী বেৰভাঙী বিদ্যালয়ৰ জাননী",
                message = "Mid-term evaluation exams will commence next week. Check syllabus in PDF library.",
                messageAs = "অহা সপ্তাহৰ পৰা অৰ্ধবাৰ্ষিক পৰীক্ষা আৰম্ভ হ'ব। ডিজিটেল লাইব্ৰেৰী চাওক।",
                type = "NOTICE",
                timestamp = System.currentTimeMillis() - 86400000,
                isRead = true
            )
        )
        for (n in notifications) {
            commDao.insertNotification(n)
        }
    }
}

package com.example.arjen.utility;

import static com.example.arjen.utility.Database.Questions.questionList;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.arjen.R;
import com.example.arjen.activities.PlayQuestion;
import com.example.arjen.activities.QuizList;
import com.example.arjen.activities.StoryList;
import com.example.arjen.utility.interfaces.QuizInterface;
import com.example.arjen.utility.interfaces.ShowListInterface;
import com.example.arjen.utility.interfaces.ShowListSecondPartInterface;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class Database {
    public static class Questions {
        public static FirebaseFirestore db = FirebaseFirestore.getInstance();
        public static List<Question> questionList = new ArrayList<Question>();

        public static Question findId(String id) {
            for (Question question: questionList) {
                if (question.id.equals(id)) {
                    return question;
                }
            }
            return null;
        }

        public static List<Question> questionsAnswered = new ArrayList<>();

        public static Question findRandomAfterListGet(Question originalQuestion) {
            questionsAnswered.add(originalQuestion);
            if (questionsAnswered.size() == questionList.size()) {
                questionsAnswered.clear();
                questionsAnswered.add(originalQuestion);
            }
            Boolean otherInSameQuiz = false;
            for (Question question: questionList) {
                if (!questionsAnswered.contains(question) && question.quizId.equals(originalQuestion.quizId)) {
                    otherInSameQuiz = true;
                    break;
                }
            }
            Random randomNew = new Random();
            int questionNumber = randomNew.nextInt(questionList.size());
            Question newQuestion = questionList.get(questionNumber);
            while (questionsAnswered.contains(newQuestion) || (otherInSameQuiz && !newQuestion.quizId.equals(originalQuestion.quizId))) {
                questionNumber = randomNew.nextInt(questionList.size());
                newQuestion = questionList.get(questionNumber);
            }
            return newQuestion;
        }

        public static void findRandom(Question originalQuestion, PlayQuestion playQuestion) {
            questionList = new ArrayList<Question>();
            db.collection("questions")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> data = document.getData();
                                    questionList.add(new Question(document.getId(),
                                            data.get("questionText").toString(),
                                            data.get("quizId").toString(),
                                            Integer.parseInt(data.get("answer").toString()),
                                            (List<String>) data.get("options")));
                                }
                                playQuestion.randomFound(findRandomAfterListGet(originalQuestion));
                            }
                        }
                    });
        }

        public static void get() {
            questionList = new ArrayList<Question>();
            db.collection("questions")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> data = document.getData();
                                    questionList.add(new Question(document.getId(),
                                            data.get("questionText").toString(),
                                            data.get("quizId").toString(),
                                            Integer.parseInt(data.get("answer").toString()),
                                            (List<String>) data.get("options")));
                                }
                            }
                        }
                    });
        }

        public static void getForQuiz(String quizId, ShowListInterface addQuiz) {
            questionList = new ArrayList<Question>();
            db.collection("questions")
                    .whereEqualTo("quizId", quizId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> data = document.getData();
                                    questionList.add(new Question(document.getId(),
                                            data.get("questionText").toString(),
                                            data.get("quizId").toString(),
                                            Integer.parseInt(data.get("answer").toString()),
                                            (List<String>) data.get("options")));
                                }
                            }
                        }
                        addQuiz.showList();
                    });
        }

        public static void getForQuizes(ShowListSecondPartInterface addQuiz) {
            questionList = new ArrayList<Question>();
            if (Quizes.quizList.size() != 0) {
                List<String> idList = new ArrayList<>();
                for (Quizes.Quiz quiz: Quizes.quizList) {
                    idList.add(quiz.id);
                }
                db.collection("questions")
                        .whereIn("quizId", idList)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (task.getResult().size() > 0) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<String, Object> data = document.getData();
                                        questionList.add(new Question(document.getId(),
                                                data.get("questionText").toString(),
                                                data.get("quizId").toString(),
                                                Integer.parseInt(data.get("answer").toString()),
                                                (List<String>) data.get("options")));
                                    }
                                }
                            }
                            addQuiz.showListSecondPart();
                        });
            } else {
                addQuiz.showListSecondPart();
            }
        }

        public static void getWithText(String text, ShowListInterface addQuiz) {
            questionList = new ArrayList<Question>();
            db.collection("questions")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> data = document.getData();
                                    if (data.get("questionText").toString().toLowerCase().contains(text.toLowerCase(Locale.ROOT))) {
                                        questionList.add(new Question(document.getId(),
                                                data.get("questionText").toString(),
                                                data.get("quizId").toString(),
                                                Integer.parseInt(data.get("answer").toString()),
                                                (List<String>) data.get("options")));
                                    }
                                }
                            }
                        }
                        addQuiz.showList();
                    });
        }

        public static void get(String id) {
            findId(id).get();
        }

        public static void add(String questionText, String quizId, int answer, List<String> options) {
            questionList.add(new Question(questionText, quizId, answer, options));
        }

        public static void set(String id, String questionText, String quizId, int answer, List<String> options) {
            findId(id).set(questionText, quizId, answer, options);
        }

        public static void delete(String id) {
            findId(id).delete();
        }

        public static void update(String id, String questionText, String quizId, int answer, List<String> options) {
            findId(id).update(questionText, quizId, answer, options);
        }

        public static class Question {
            public String id;
            public String questionText;
            public String quizId;
            public int answer;
            public List<String> options;
            private QuizInterface quizInterface;
            private Activity activity;
            private int deletePosition;

            private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            delete();
                            if (quizInterface != null) {
                                quizInterface.deleteQuestion(deletePosition);
                            } else {
                                activity.finish();
                                activity.onBackPressed();
                            }
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            public void startDelete(QuizInterface quizInterface, int deletePosition) {
                this.activity = null;
                this.quizInterface = quizInterface;
                this.deletePosition = deletePosition;
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) quizInterface);
                builder.setMessage(((Context) quizInterface).getApplicationContext().getResources().getString(R.string.question_delete))
                        .setPositiveButton(((Context) quizInterface).getApplicationContext().getResources().getString(R.string.yes), dialogClickListener)
                        .setNegativeButton(((Context) quizInterface).getApplicationContext().getResources().getString(R.string.no), dialogClickListener).show();
            }

            public void startDelete(Activity activity) {
                this.activity = activity;
                this.quizInterface = null;
                this.deletePosition = -1;
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(activity.getApplicationContext().getResources().getString(R.string.question_delete))
                        .setPositiveButton(activity.getApplicationContext().getResources().getString(R.string.yes), dialogClickListener)
                        .setNegativeButton(activity.getApplicationContext().getResources().getString(R.string.no), dialogClickListener).show();
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Question question = (Question) o;
                return Objects.equals(id, question.id);
            }

            @Override
            public int hashCode() {
                return Objects.hash(id);
            }

            public void get() {
                db.collection("questions")
                        .document(id)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    questionText = document.getString("questionText");
                                    quizId = document.getString("quizId");
                                    answer = (int) document.getDouble("answer").doubleValue();
                                    options = (List<String>) document.get("options");
                                }
                            }
                        });
            }

            public Map put() {
                Map<String, Object> data = new HashMap<>();
                data.put("questionText", questionText);
                data.put("quizId", quizId);
                data.put("answer", answer);
                data.put("options", options);
                return data;
            }

            public void delete() {
                db.collection("questions")
                        .document(id)
                        .delete();
                questionList.remove(this);
            }

            public void update() {
                db.collection("questions")
                        .document(id)
                        .set(put());
            }

            public void update(String questionText, String quizId, int answer, List<String> options) {
                set(questionText, quizId, answer, options);
                update();
            }

            public void add() {
                db.collection("questions")
                        .add(put())
                        .addOnSuccessListener(aVoid -> {
                            id = aVoid.getId();
                        });
            }

            public void set(String questionText, String quizId, int answer, List<String> options) {
                this.questionText = questionText;
                this.quizId = quizId;
                this.answer = answer;
                this.options = options;
            }

            Question(String questionText, String quizId, int answer, List<String> options) {
                set(questionText, quizId, answer, options);
                add();
            }

            Question(String id, String questionText, String quizId, int answer, List<String> options) {
                this.id = id;
                set(questionText, quizId, answer, options);
            }
        }
    }

    public static class Quizes {
        public static FirebaseFirestore db = FirebaseFirestore.getInstance();
        public static List<Quiz> quizList = new ArrayList<Quiz>();

        public static Quiz findId(String id) {
            for (Quiz quiz: quizList) {
                if (quiz.id.equals(id)) {
                    return quiz;
                }
            }
            return null;
        }

        public static void get(ShowListInterface quizListActivity) {
            quizList = new ArrayList<Quiz>();
            db.collection("quizes")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> data = document.getData();
                                    quizList.add(new Quiz(
                                            document.getId(),
                                            data.get("title").toString(),
                                            data.get("subject").toString()));
                                }
                            }
                        }
                        quizListActivity.showList();
                    });
        }

        public static void getWithTitle(String title, ShowListInterface quizListActivity) {
            quizList = new ArrayList<Quiz>();
            if (title == null || title.length() == 0) {
                get(quizListActivity);
                return;
            }
            db.collection("quizes")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> data = document.getData();
                                    if (data.get("title").toString().toLowerCase().contains(title.toLowerCase(Locale.ROOT))) {
                                        quizList.add(new Quiz(
                                                document.getId(),
                                                data.get("title").toString(),
                                                data.get("subject").toString()));
                                    }
                                }
                            }
                        }
                        quizListActivity.showList();
                    });
        }

        public static void getWithSubject(String subject, ShowListInterface quizListActivity) {
            quizList = new ArrayList<Quiz>();
            if (subject == null || subject.length() == 0) {
                get(quizListActivity);
                return;
            }
            db.collection("quizes")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> data = document.getData();
                                    if (data.get("subject").toString().toLowerCase().contains(subject.toLowerCase(Locale.ROOT))) {
                                        quizList.add(new Quiz(
                                                document.getId(),
                                                data.get("title").toString(),
                                                data.get("subject").toString()));
                                    }
                                }
                            }
                        }
                        quizListActivity.showList();
                    });
        }

        public static void get(String id) {
            findId(id).get();
        }

        public static void add(String title, String subject) {
            quizList.add(new Quiz(title, subject));
        }

        public static void set(String id, String title, String subject) {
            findId(id).set(title, subject);
        }

        public static void delete(String id) {
            findId(id).delete();
        }

        public static void update(String id, String title, String subject) {
            findId(id).update(title, subject);
        }

        public static class Quiz {
            public String id;
            public String title, subject;
            private QuizList myQuizList;
            private Activity activity;
            private int deletePosition;

            private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            delete();
                            if (myQuizList != null) {
                                myQuizList.deleteQuiz(deletePosition);
                            } else {
                                activity.finish();
                                activity.onBackPressed();
                            }
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            public void startDelete(QuizList myQuizList, int deletePosition) {
                this.activity = null;
                this.myQuizList = myQuizList;
                this.deletePosition = deletePosition;
                AlertDialog.Builder builder = new AlertDialog.Builder(myQuizList);
                builder.setMessage(myQuizList.getApplicationContext().getResources().getString(R.string.quiz_delete))
                        .setPositiveButton(myQuizList.getApplicationContext().getResources().getString(R.string.yes), dialogClickListener)
                        .setNegativeButton(myQuizList.getApplicationContext().getResources().getString(R.string.no), dialogClickListener).show();
            }

            public void startDelete(Activity activity) {
                this.activity = activity;
                this.myQuizList = null;
                this.deletePosition = -1;
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(activity.getApplicationContext().getResources().getString(R.string.quiz_delete))
                        .setPositiveButton(activity.getApplicationContext().getResources().getString(R.string.yes), dialogClickListener)
                        .setNegativeButton(activity.getApplicationContext().getResources().getString(R.string.no), dialogClickListener).show();
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Quizes.Quiz quiz = (Quizes.Quiz) o;
                return Objects.equals(id, quiz.id);
            }

            public void get() {
                db.collection("quizes")
                        .document(id)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    title = document.getString("title");
                                    subject = document.getString("subject");
                                }
                            }
                        });
            }

            public Map put() {
                Map<String, Object> data = new HashMap<>();
                data.put("title", title);
                data.put("subject", subject);
                return data;
            }

            public void delete() {
                db.collection("quizes")
                        .document(id)
                        .delete();
                db.collection("questions")
                        .whereEqualTo("quizId", id)
                        .get()
                        .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().size() > 0) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String questionId = document.getId();
                                                db.collection("questions")
                                                        .document(questionId)
                                                        .delete();
                                                questionList.remove(Questions.findId(questionId));
                                            }
                                        }
                                    }
                                });
                quizList.remove(this);
            }

            public void update() {
                db.collection("quizes")
                        .document(id)
                        .set(put());
            }

            public void update(String title, String subject) {
                set(title, subject);
                update();
            }

            public void add() {
                db.collection("quizes")
                        .add(put())
                        .addOnSuccessListener(aVoid -> {
                            id = aVoid.getId();
                        });
            }

            public void set(String title, String subject) {
                this.title = title;
                this.subject = subject;
            }

            Quiz(String title, String subject) {
                set(title, subject);
                add();
            }

            Quiz(String id, String title, String subject) {
                this.id = id;
                set(title, subject);
            }
        }
    }

    public static class Stories {
        public static FirebaseFirestore db = FirebaseFirestore.getInstance();
        public static List<Story> storyList = new ArrayList<Story>();

        public static Story findId(String id) {
            for (Story story: storyList) {
                if (story.id.equals(id)) {
                    return story;
                }
            }
            return null;
        }

        public static void get(ShowListInterface storyListActivity) {
            storyList = new ArrayList<Story>();
            db.collection("stories")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> data = document.getData();
                                    storyList.add(new Story(
                                            document.getId(),
                                            data.get("title").toString(),
                                            data.get("storyText").toString(),
                                            (List<String>) data.get("questions")));
                                }
                            }
                        }
                        storyListActivity.showList();
                    });
        }

        public static void getWithTitle(String title, ShowListInterface storyListActivity) {
            storyList = new ArrayList<Story>();
            if (title == null || title.length() == 0) {
                get(storyListActivity);
                return;
            }
            db.collection("stories")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> data = document.getData();
                                    if (data.get("title").toString().toLowerCase().contains(title.toLowerCase(Locale.ROOT))) {
                                        storyList.add(new Story(
                                                document.getId(),
                                                data.get("title").toString(),
                                                data.get("storyText").toString(),
                                                (List<String>) data.get("questions")));
                                    }
                                }
                            }
                        }
                        storyListActivity.showList();
                    });
        }

        public static void get(String id) {
            findId(id).get();
        }

        public static void add(String title, String storyText, List<String> questions) {
            storyList.add(new Story(title, storyText, questions));
        }

        public static void set(String id, String title, String storyText, List<String> questions) {
            findId(id).set(title, storyText, questions);
        }

        public static void delete(String id) {
            findId(id).delete();
        }

        public static void update(String id, String title, String storyText, List<String> questions) {
            findId(id).update(title, storyText, questions);
        }

        public static class Story {
            public String id;
            public String title, storyText;
            public List<String> questions;
            private StoryList myStoryList;
            private Activity activity;
            private int deletePosition;

            private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            delete();
                            if (myStoryList != null) {
                                myStoryList.deleteStory(deletePosition);
                            } else {
                                activity.finish();
                                activity.onBackPressed();
                            }
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            public void startDelete(StoryList myStoryList, int deletePosition) {
                this.activity = null;
                this.myStoryList = myStoryList;
                this.deletePosition = deletePosition;
                AlertDialog.Builder builder = new AlertDialog.Builder(myStoryList);
                builder.setMessage(myStoryList.getApplicationContext().getResources().getString(R.string.story_delete))
                        .setPositiveButton(myStoryList.getApplicationContext().getResources().getString(R.string.yes), dialogClickListener)
                        .setNegativeButton(myStoryList.getApplicationContext().getResources().getString(R.string.no), dialogClickListener).show();
            }

            public void startDelete(Activity activity) {
                this.activity = activity;
                this.myStoryList = null;
                this.deletePosition = -1;
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(activity.getApplicationContext().getResources().getString(R.string.story_delete))
                        .setPositiveButton(activity.getApplicationContext().getResources().getString(R.string.yes), dialogClickListener)
                        .setNegativeButton(activity.getApplicationContext().getResources().getString(R.string.no), dialogClickListener).show();
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Stories.Story story = (Stories.Story) o;
                return Objects.equals(id, story.id);
            }

            public void get() {
                db.collection("stories")
                        .document(id)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    title = document.getString("title");
                                    storyText = document.getString("storyText");
                                    questions = (List<String>) document.get("questions");
                                }
                            }
                        });
            }

            public Map put() {
                Map<String, Object> data = new HashMap<>();
                data.put("title", title);
                data.put("storyText", storyText);
                data.put("questions", questions);
                return data;
            }

            public void delete() {
                db.collection("stories")
                        .document(id)
                        .delete();
                storyList.remove(this);
            }

            public void update() {
                db.collection("stories")
                        .document(id)
                        .set(put());
            }

            public void update(String title, String storyText, List<String> questions) {
                set(title, storyText, questions);
                update();
            }

            public void add() {
                db.collection("stories")
                        .add(put())
                        .addOnSuccessListener(aVoid -> {
                            id = aVoid.getId();
                        });
            }

            public void set(String title, String storyText, List<String> questions) {
                this.title = title;
                this.storyText = storyText;
                this.questions = questions;
            }

            Story(String title, String storyText, List<String> questions) {
                set(title, storyText, questions);
                add();
            }

            Story(String id, String title, String storyText, List<String> questions) {
                this.id = id;
                set(title, storyText, questions);
            }
        }
    }
}

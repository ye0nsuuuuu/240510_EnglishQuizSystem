package EnglishQuiz;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class EnglishQuizSystem extends JFrame {
    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USER = "scott";
    private static final String PASSWORD = "tiger";
    private Connection conn;
    private Statement stmt;
    private ResultSet rs;
    private JLabel wordLabel;
    private JTextField answerField;
    private JButton submitButton;
    private JButton showAnswerButton;
    // private JButton addButton; // 단어 추가 버튼 삭제
    private String currentWord;
    private String currentMeaning;

    public EnglishQuizSystem() {
        initializeDB();
        initializeUI();
        loadNextQuiz();
    }

    // 데이터베이스 초기화
    private void initializeDB() {
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // UI 초기화
    private void initializeUI() {
        setTitle("English Quiz Program"); // 프로그램 창 제목 설정
        setSize(400, 250); // 프로그램 창 크기 설정
        setDefaultCloseOperation(EXIT_ON_CLOSE); // 프로그램 창을 닫을 때 프로그램 종료
        setLocationRelativeTo(null); // 프로그램을 화면 가운데에 위치

        JPanel panel = new JPanel(new BorderLayout()); // 전체 패널 생성

        wordLabel = new JLabel(); // 라벨 생성
        answerField = new JTextField(20); // 텍스트 필드 생성
        submitButton = new JButton("제출하기"); // 제출 버튼 생성
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAnswer(); // 제출 버튼 클릭 시 답 확인
            }
        });
        showAnswerButton = new JButton("정답 보기"); // 정답 보기 버튼 생성
        showAnswerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAnswer(); // 정답 보기 버튼 클릭 시 정답 보기
            }
        });

        // addButton = new JButton("Add Word"); // 단어 추가 버튼 삭제

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2)); // 버튼 패널 생성 (2x1 그리드 레이아웃)
        buttonPanel.add(submitButton); // 버튼 패널에 제출 버튼 추가
        buttonPanel.add(showAnswerButton); // 버튼 패널에 정답 보기 버튼 추가
        // buttonPanel.add(addButton); // 단어 추가 버튼 삭제

        panel.add(wordLabel, BorderLayout.NORTH); // 전체 패널에 라벨을 북쪽에 추가
        panel.add(answerField, BorderLayout.CENTER); // 전체 패널에 답 입력 필드를 중앙에 추가
        panel.add(buttonPanel, BorderLayout.SOUTH); // 전체 패널에 버튼 패널을 남쪽에 추가

        add(panel); // 전체 패널을 프레임에 추가
    }

    // 다음 퀴즈 로드
    private void loadNextQuiz() {
        try {
            String query = "SELECT word, meaning FROM words ORDER BY DBMS_RANDOM.RANDOM";
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                currentWord = rs.getString("word");
                currentMeaning = rs.getString("meaning");
                wordLabel.setText("단어의 뜻을 입력하세요: " + currentWord); // 현재 단어를 라벨에 설정
                answerField.setText(""); // 답 입력 필드 초기화
            } else {
                JOptionPane.showMessageDialog(null, "No words found in the database."); // 데이터베이스에 단어가 없는 경우 메시지 표시
                System.exit(0); // 프로그램 종료
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 답 확인
    private void checkAnswer() {
        String userAnswer = answerField.getText().trim();
        if (userAnswer.equals(currentMeaning)) {
            JOptionPane.showMessageDialog(null, "정답입니다!"); // 사용자의 답이 정답과 일치하는 경우 메시지 표시
        } else {
            int option = JOptionPane.showConfirmDialog(null, "오답입니다 다시 시도해 보세요", null, JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                answerField.setText(""); // YES를 선택한 경우 답 입력 필드 초기화
                return;
            } else {
                JOptionPane.showMessageDialog(null, "정답은 " + currentMeaning + " 입니다."); // NO를 선택한 경우 정답 표시
            }
        }
        loadNextQuiz(); // 다음 퀴즈 로드
    }

    // 정답 보기
    private void showAnswer() {
        JOptionPane.showMessageDialog(null, "정답은 " + currentMeaning + " 입니다."); // 정답 메시지 표시
    }

    /*
    private void addNewWord() {
        String newWord = JOptionPane.showInputDialog("새로운 단어를 입력하세요:");
        String newMeaning = JOptionPane.showInputDialog("새로운 단어의 뜻을 입력하세요:");

        try {
            String query = "INSERT INTO words (word, meaning) VALUES ('" + newWord + "', '" + newMeaning + "')";
            stmt.executeUpdate(query);
            JOptionPane.showMessageDialog(null, "새로운 단어가 성공적으로 추가되었습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "단어 추가 중 오류가 발생했습니다.");
        }
    }
    */
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new EnglishQuizSystem().setVisible(true); // EnglishQuizSystem 객체를 생성하고 화면에 표시
            }
        });
    }
}

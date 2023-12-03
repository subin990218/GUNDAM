package com.mobilesuit.clientplugin.memo;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBScrollPane;
import com.mobilesuit.clientplugin.singleton.DataContainer;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MemoBox {
    private JTextArea textArea;
    private JPanel panel;
    private final DataContainer dataContainer = DataContainer.getInstance();
    private AnActionEvent actionEvent;

    public MemoBox(AnActionEvent e) {
        this.actionEvent = e;

        JDialog dialog = new JDialog();
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(600, 400);

        Font newFont = new Font("SansSerif", Font.PLAIN, 20);

        String[] fontSizeOptions = {"12", "16", "20", "24"};
        ComboBox<String> fontSizeComboBox = new ComboBox<>(fontSizeOptions);

        fontSizeComboBox.setSelectedItem("20");
        fontSizeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedSize = (String) fontSizeComboBox.getSelectedItem();
                int fontSize = Integer.parseInt(selectedSize);

                Font currentFont = textArea.getFont();
                textArea.setFont(new Font(currentFont.getFontName(), currentFont.getStyle(), fontSize));
            }
        });

        textArea = new JTextArea(10, 40);
        JBScrollPane scrollPane = new JBScrollPane(textArea);

        JPanel controlPanel = new JPanel();
        textArea.setFont(newFont);
        controlPanel.add(new JLabel("Font Size:"));
        controlPanel.add(fontSizeComboBox);

        JButton fetchButton = new JButton("이슈 가져오기");
        fetchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchAndDisplayGitHubIssues();
            }
        });

        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        dialog.setLocation(screenWidth - dialog.getWidth(), 200);

        dialog.add(controlPanel, BorderLayout.NORTH);
        dialog.add(fetchButton, BorderLayout.SOUTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.setAlwaysOnTop(true);

        dialog.setVisible(true);
    }

    private void fetchAndDisplayGitHubIssues() {
        String token = dataContainer.getGitHubAccessToken();
        String username = dataContainer.getUserId();
        String repositoryName = getGitHubRepositoryName();
        System.out.println(token);
        System.out.println(username);
        System.out.println(repositoryName);
        fetchAndDisplayGitHubIssues(token, repositoryName, username);
    }

    private String removePrefixAndSuffix(String url) {
        String prefixToRemove = "https://github.com/";
        String suffixToRemove = ".git";

        if (url.startsWith(prefixToRemove)) {
            url = url.substring(prefixToRemove.length());
        }

        if (url.endsWith(suffixToRemove)) {
            url = url.substring(0, url.length() - suffixToRemove.length());
        }

        return url;
    }

    private String getGitHubRepositoryName() {
        Project project = actionEvent.getProject();

        if (project != null) {
            String projectBasePath = project.getBasePath();

            if (projectBasePath != null) {
                try {
                    Repository repository = Git.open(new File(projectBasePath)).getRepository();

                    String remoteUrl = repository.getConfig().getString("remote", "origin", "url");

                    remoteUrl = removePrefixAndSuffix(remoteUrl);

                    return remoteUrl;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return null;
    }

    private void fetchAndDisplayGitHubIssues(String token, String repositoryName, String userName) {
        try {
            GitHub github = GitHub.connectUsingOAuth(token);
            GHRepository repository = github.getRepository(repositoryName);

            List<GHIssue> allIssues = repository.getIssues(GHIssueState.OPEN);
            List<GHIssue> filteredIssues = new ArrayList<>();

            for (GHIssue issue : allIssues) {
                if (issue.getAssignee() != null && issue.getAssignee().getLogin().equals(userName)) {
                    filteredIssues.add(issue);
                }
            }

            textArea.setText("");

            for (GHIssue issue : filteredIssues) {
                textArea.append("Issue #" + issue.getNumber() + " : " + issue.getTitle() + "\n");
                textArea.append(issue.getBody() + "\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            textArea.setText("GitHub 이슈 가져오기 오류: " + ex.getMessage());
        }
    }
}

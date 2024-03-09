package notebook.model.repository.impl;

import notebook.model.User;
import notebook.model.repository.GBRepository;
import notebook.util.mapper.impl.UserMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository implements GBRepository {
    private final UserMapper mapper;
    private final String fileName;

    public UserRepository(String fileName) {
        this.mapper = new UserMapper();
        this.fileName = fileName;
        createFileIfNotExists(fileName);
    }

    private static class FileOperation {
        private final String fileName;

        public FileOperation(String fileName) {
            this.fileName = fileName;
        }

        public List<String> readAll() {
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return lines;
        }

        public void saveAll(List<String> data) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false))) {
                for (String line : data) {
                    writer.write(line);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<User> findAll() {
        List<String> lines = new FileOperation(fileName).readAll();
        List<User> users = new ArrayList<>();
        for (String line : lines) {
            users.add(mapper.toOutput(line));
        }
        return users;
    }

    @Override
    public User create(User user) {
        List<User> users = findAll();
        long max = users.stream().mapToLong(User::getId).max().orElse(0L);
        long next = max + 1;
        user.setId(next);
        users.add(user);
        write(users);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        List<User> users = findAll();
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<User> update(Long userId, User update) {
        List<User> users = findAll();
        for (User user : users) {
            if (user.getId().equals(userId)) {
                if (!update.getFirstName().isEmpty()) {
                    user.setFirstName(update.getFirstName());
                }
                if (!update.getLastName().isEmpty()) {
                    user.setLastName(update.getLastName());
                }
                if (!update.getPhone().isEmpty()) {
                    user.setPhone(update.getPhone());
                }
                write(users);
                return Optional.of(update);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean delete(Long id) {
        List<User> users = findAll();
        boolean removed = users.removeIf(user -> user.getId().equals(id));
        if (removed) {
            write(users);
        }
        return removed;
    }

    private void write(List<User> users) {
        List<String> lines = new ArrayList<>();
        for (User u : users) {
            lines.add(mapper.toInput(u));
        }
        new FileOperation(fileName).saveAll(lines);
    }

    private void createFileIfNotExists(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void saveAll(List<String> data) {
        new FileOperation(fileName).saveAll(data);
    }

    @Override
    public List<String> readAll() {
        return new FileOperation(fileName).readAll();
    }
}

package data;

import model.Credential;
import security.EncryptionUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static final String DATA_DIR = "data";
    private static final String HASH_FILE = DATA_DIR + "/master.hash";
    private static final String VAULT_FILE = DATA_DIR + "/passwords.enc";

    public DataManager() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public boolean isFirstTimeUser() {
        return !new File(HASH_FILE).exists();
    }

    public void saveMasterHash(String hash) throws IOException {
        Files.write(Paths.get(HASH_FILE), hash.getBytes());
    }

    public String loadMasterHash() throws IOException {
        return new String(Files.readAllBytes(Paths.get(HASH_FILE)));
    }

    public void saveVault(List<Credential> vault, String masterPassword) throws Exception {
        File file = new File(VAULT_FILE);
        EncryptionUtil.encryptAndSave(vault, masterPassword, file);
    }

    public List<Credential> loadVault(String masterPassword) throws Exception {
        File file = new File(VAULT_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        return EncryptionUtil.decryptAndLoad(masterPassword, file);
    }
}

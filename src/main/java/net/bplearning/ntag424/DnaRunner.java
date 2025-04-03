package net.bplearning.ntag424;

import net.bplearning.ntag424.card.KeyInfo;
import net.bplearning.ntag424.card.KeySet;
import net.bplearning.ntag424.command.*;
import net.bplearning.ntag424.constants.Ntag424;
import net.bplearning.ntag424.constants.Permissions;
import net.bplearning.ntag424.encryptionmode.AESEncryptionMode;
import net.bplearning.ntag424.sdm.NdefTemplateMaster;
import net.bplearning.ntag424.sdm.SDMSettings;
import net.bplearning.ntag424.util.ThrowableFunction;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.Logger;

import javax.smartcardio.*;
import java.io.IOException;
import java.util.List;
import java.nio.charset.StandardCharsets;

public class DnaRunner {
    private static final Logger logger = org.apache.logging.log4j.LogManager.getLogger(DnaRunner.class);

//    private DnaCommunicator communicator;

    private Card card;

    private DnaCommunicator connect() throws CardException {
        // 获取终端工厂
        TerminalFactory factory = TerminalFactory.getDefault();
        // 获取读写器列表
        List<CardTerminal> terminals = factory.terminals().list();

        if (terminals.isEmpty()) {
            throw new CardException("No terminals found");
        }

        // 选择第一个读写器
        CardTerminal terminal = terminals.get(0);
        logger.info("使用的读写器： " + terminal.getName());

        // 建立与读写器的连接
        this.card = terminal.connect("*");
        logger.info("卡片ATR: " + Hex.encodeHexString(this.card.getATR().getBytes()));
        CardChannel channel = card.getBasicChannel();
        ThrowableFunction<byte[], byte[], IOException> transceiver = new SmartcardIOTransceiver(channel);


        // Initialize DNA library
        DnaCommunicator communicator = new DnaCommunicator();
        communicator.setTransceiver(transceiver);
        communicator.setLogger((info) -> logger.info("[DNACOMM] " + info));
        return communicator;
    }

    private void close() throws CardException {
        if (this.card == null) return;
        this.card.disconnect(false);
        this.card = null;
    }

    public KeySet getKeySet() {
        // NOTE - replace these with your own keys.
        //
        //        Any of the keys *can* be diversified
        //        if you don't use RandomID, but usually
        //        only the MAC key is diversified.

        KeySet keySet = new KeySet();
        keySet.setUsesLrp(false);

        // This is the "master" key
        KeyInfo key0 = new KeyInfo();
        key0.diversifyKeys = false;
        key0.key = Ntag424.FACTORY_KEY;
        keySet.setKey(Permissions.ACCESS_KEY0, key0);

        // No standard usage
        KeyInfo key1 = new KeyInfo();
        key1.diversifyKeys = false;
        key1.key = Ntag424.FACTORY_KEY;
        keySet.setKey(Permissions.ACCESS_KEY1, key1);

        // Usually used as a meta read key for encrypted PICC data
        KeyInfo key2 = new KeyInfo();
        key2.diversifyKeys = false;
        key2.key = Ntag424.FACTORY_KEY;
        keySet.setKey(Permissions.ACCESS_KEY2, key2);

        // Usually used as the MAC and encryption key.
        // The MAC key usually has the diversification information setup.
        KeyInfo key3 = new KeyInfo();
        key3.diversifyKeys = true;
        key3.systemIdentifier = "testing".getBytes(StandardCharsets.UTF_8); // systemIdentifier is usually a hex-encoded string based on the name of your intended use.
        key3.version = 1; // Since it is not a factory key (it is *based* on a factory key, but underwent diversification), need to set to a version number other than 0.
        key3.key = Ntag424.FACTORY_KEY;

        // No standard usage
        keySet.setKey(Permissions.ACCESS_KEY3, key3);
        KeyInfo key4 = new KeyInfo();
        key4.diversifyKeys = false;
        key4.key = Ntag424.FACTORY_KEY;
        keySet.setKey(Permissions.ACCESS_KEY4, key4);

        // This is used for decoding, but documenting that key2/key3 are standard for meta and mac
        keySet.setMetaKey(Permissions.ACCESS_KEY2);
        keySet.setMacFileKey(Permissions.ACCESS_KEY3);

        return keySet;
    }

    public String debugStringForFileSettings(FileSettings fs) {
        StringBuilder sb = new StringBuilder();
        sb.append("= FileSettings =").append("\n");
        sb.append("fileType: ").append("n/a").append("\n"); // todo expose get file type for DESFire
        sb.append("commMode: ").append(fs.commMode.toString()).append("\n");
        sb.append("accessRights RW:       ").append(fs.readWritePerm).append("\n");
        sb.append("accessRights CAR:      ").append(fs.changePerm).append("\n");
        sb.append("accessRights R:        ").append(fs.readPerm).append("\n");
        sb.append("accessRights W:        ").append(fs.writePerm).append("\n");
        sb.append("fileSize: ").append(fs.fileSize).append("\n");
        sb.append("= Secure Dynamic Messaging =").append("\n");
        sb.append("isSdmEnabled: ").append(fs.sdmSettings.sdmEnabled).append("\n");
        sb.append("isSdmOptionUid: ").append(fs.sdmSettings.sdmOptionUid).append("\n");
        sb.append("isSdmOptionReadCounter: ").append(fs.sdmSettings.sdmOptionReadCounter).append("\n");
        sb.append("isSdmOptionReadCounterLimit: ").append(fs.sdmSettings.sdmOptionReadCounterLimit).append("\n");
        sb.append("isSdmOptionEncryptFileData: ").append(fs.sdmSettings.sdmOptionEncryptFileData).append("\n");
        sb.append("isSdmOptionUseAscii: ").append(fs.sdmSettings.sdmOptionUseAscii).append("\n");
        sb.append("sdmMetaReadPerm:             ").append(fs.sdmSettings.sdmMetaReadPerm).append("\n");
        sb.append("sdmFileReadPerm:             ").append(fs.sdmSettings.sdmFileReadPerm).append("\n");
        sb.append("sdmReadCounterRetrievalPerm: ").append(fs.sdmSettings.sdmReadCounterRetrievalPerm).append("\n");
        sb.append("sdmUidOffset:         ").append(fs.sdmSettings.sdmUidOffset).append("\n");
        sb.append("sdmReadCounterOffset: ").append(fs.sdmSettings.sdmReadCounterOffset).append("\n");
        sb.append("sdmPiccDataOffset:    ").append(fs.sdmSettings.sdmPiccDataOffset).append("\n");
        sb.append("sdmMacInputOffset:    ").append(fs.sdmSettings.sdmMacInputOffset).append("\n");
        sb.append("sdmMacOffset:         ").append(fs.sdmSettings.sdmMacOffset).append("\n");
        sb.append("sdmEncOffset:         ").append(fs.sdmSettings.sdmEncOffset).append("\n");
        sb.append("sdmEncLength:         ").append(fs.sdmSettings.sdmEncLength).append("\n");
        sb.append("sdmReadCounterLimit:  ").append(fs.sdmSettings.sdmReadCounterLimit).append("\n");
        return sb.toString();
    }


    public static void main(String[] args) {
        try {
            DnaRunner runner = new DnaRunner();
            DnaCommunicator communicator = runner.connect();

            // Synchronize keys first
            KeySet keySet = runner.getKeySet();
            keySet.synchronizeKeys(communicator);

            // Authenticate with a key.  If you are in LRP mode (Requires permanently changing tag settings), uncomment the LRP version instead.
            // if(LRPEncryptionMode.authenticateLRP(communicator, 0, Constants.FACTORY_KEY)) {
            if (AESEncryptionMode.authenticateEV2(communicator, 0, keySet.getKey(0).key)) { // Assumes key0 is non-diversified
                logger.info("login success");
                byte[] cardUid = GetCardUid.run(communicator);
                logger.info("Card UID: " + Hex.encodeHexString(cardUid));
//                int keyVersion = GetKeyVersion.run(communicator, 0);
//                logger.info("Key 0 version: " + keyVersion);

                // Doing this will set LRP mode for all future authentications
                // SetCapabilities.run(communicator, true);

                // Get the NDEF file settings
                FileSettings ndeffs = GetFileSettings.run(communicator, Ntag424.NDEF_FILE_NUMBER);
                logger.info("Debug NDEF: " + runner.debugStringForFileSettings(ndeffs));

                // Secret data
                byte[] secretData = new byte[]{
                        1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16
                };

                // Set the access keys and options
                SDMSettings sdmSettings = new SDMSettings();
                sdmSettings.sdmMetaReadPerm = Permissions.ACCESS_KEY2;     // Set to a key to get encrypted PICC data (usually non-diversified since you don't know the UID until after decryption)
                sdmSettings.sdmFileReadPerm = Permissions.ACCESS_KEY3;     // Used to create the MAC and Encrypt FileData
                sdmSettings.sdmOptionUid = true;
                sdmSettings.sdmOptionReadCounter = true;

                // NDEF SDM formatter helper - uses a template to write SDMSettings and get file data
                NdefTemplateMaster master = new NdefTemplateMaster();
                master.usesLRP = false;

                byte[] ndefRecord = master.generateNdefTemplateFromUrlString("https://www.example.com/{PICC}/{FILE}/{MAC}", secretData, sdmSettings);
                // This link (not by me) has a handy decoder if you are using factory keys (we are using a diversified factory key, so this will not work unless you change that in the keyset):
                // byte[] ndefRecord = master.generateNdefTemplateFromUrlString("https://sdm.nfcdeveloper.com/tagpt?uid={UID}&ctr={COUNTER}&cmac={MAC}", sdmSettings);

                // Write the record to the file
                WriteData.run(communicator, Ntag424.NDEF_FILE_NUMBER, ndefRecord);

                // Set the general NDEF permissions
                ndeffs.readPerm = Permissions.ACCESS_EVERYONE;
                ndeffs.writePerm = Permissions.ACCESS_KEY0;
                ndeffs.readWritePerm = Permissions.ACCESS_KEY3; // backup key
                ndeffs.changePerm = Permissions.ACCESS_KEY0;
                ndeffs.sdmSettings = sdmSettings; // Use the SDM settings we just setup
                logger.info("New Ndef Settings: " + runner.debugStringForFileSettings(ndeffs));
                ChangeFileSettings.run(communicator, Ntag424.NDEF_FILE_NUMBER, ndeffs);
                logger.info("Tag Sync Successful");
            } else {
                logger.info("Login unsuccessful");
                logger.info("invalid application key");
            }

            // 断开与读写器的连接
            runner.close();

        } catch (CardException e) {
            System.out.println("发生错误： " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

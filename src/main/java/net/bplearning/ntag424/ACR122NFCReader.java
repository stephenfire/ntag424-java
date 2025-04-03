package net.bplearning.ntag424;

import javax.smartcardio.*;
import java.util.List;

public class ACR122NFCReader {

    public static void main(String[] args) {
        try {
            // 获取终端工厂
            TerminalFactory factory = TerminalFactory.getDefault();
            // 获取读写器列表
            List<CardTerminal> terminals = factory.terminals().list();

            if (terminals.isEmpty()) {
                System.out.println("没有找到智能卡读写器。");
                return;
            }

            // 选择第一个读写器
            CardTerminal terminal = terminals.get(0);
            System.out.println("使用的读写器： " + terminal.getName());

            // 建立与读写器的连接
            Card card = terminal.connect("*");
            System.out.println("卡片ATR: " + bytesToHex(card.getATR().getBytes()));

            // 用于获取 UID 的 APDU 命令
//            CommandAPDU command = new CommandAPDU(0xFF, 0xCA, 0x00, 0x00, 0x00);
            CommandAPDU command = new CommandAPDU(0x90, 0x71, 0x00, 0x00, new byte[]{0x00, 0x00}, 0x00, 0x02);
            CardChannel channel = card.getBasicChannel();
            ResponseAPDU response = channel.transmit(command);

            if (response.getSW1() == 0x90 && response.getSW2() == 0x00) {
                // 成功，将 UID 转换为十六进制字符串
                String uid = bytesToHex(response.getData());
                System.out.println("UID: " + uid);
            } else {
                // 失败，打印错误信息
                System.out.println("获取 UID 失败。状态字： " + Integer.toHexString(response.getSW1()) + " " + Integer.toHexString(response.getSW2()));
            }

            // 断开与读写器的连接
            card.disconnect(false);

        } catch (CardException e) {
            System.out.println("发生错误： " + e.getMessage());
        }
    }

    // 将字节数组转换为十六进制字符串
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
package net.bplearning.ntag424;

import net.bplearning.ntag424.util.ThrowableFunction;

import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import java.io.IOException;
import java.nio.ByteBuffer;

public class SmartcardIOTransceiver implements ThrowableFunction<byte[], byte[], IOException> {

    private CardChannel channel;

    public SmartcardIOTransceiver(CardChannel channel) {
        this.channel = channel;
    }

    @Override
    public byte[] apply(byte[] input) throws IOException {
        ByteBuffer command = ByteBuffer.wrap(input);
        ByteBuffer response = ByteBuffer.allocate(1024); // 分配足够的缓冲区
        try {
            int responseLength = this.channel.transmit(command, response);
            byte[] byteArray = new byte[responseLength];
            response.flip().get(byteArray);
            return byteArray;
        } catch (CardException e) {
            throw new IOException(e);
        }
    }
}

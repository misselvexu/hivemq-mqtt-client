/*
 * Copyright 2018 dc-square and the HiveMQ MQTT Client Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hivemq.client.internal.mqtt.codec.decoder.mqtt3;

import com.google.common.primitives.Bytes;
import com.hivemq.client.internal.mqtt.codec.decoder.MqttMessageDecoders;
import com.hivemq.client.internal.mqtt.message.publish.pubcomp.MqttPubComp;
import com.hivemq.client.mqtt.mqtt3.message.Mqtt3MessageType;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class Mqtt3PubCompDecoderTest extends AbstractMqtt3DecoderTest {

    private static final @NotNull byte[] WELLFORMED_PUBCOMP_BEGIN = {
            //   type, flags
            0b0111_0000,
            //remaining length
            0b0000_0010
    };
    private static final @NotNull byte[] MALFORMED_PUBCOMP_BEGIN_WRONG_FLAGS = {
            //   type, flags
            0b0111_0100,
            //remaining length
            0b0000_0010
    };
    private static final @NotNull byte[] MALFORMED_PUBCOMP_BEGIN_TOO_LONG_LENGTH = {
            //   type, flags
            0b0111_0100,
            //remaining length
            0b0000_0011
    };
    private static final @NotNull byte[] ENDING_TOO_LONG_MALFORMED = {0x01};
    private static final @NotNull byte[] MAX_PACKET_ID = {(byte) 0b1111_1111, (byte) 0b1111_1111};
    private static final @NotNull byte[] MIN_PACKET_ID = {0x00, 0x00};

    Mqtt3PubCompDecoderTest() {
        super(new MqttMessageDecoders() {{
            decoders[Mqtt3MessageType.PUBCOMP.getCode()] = new Mqtt3PubCompDecoder();
        }});
    }

    @ParameterizedTest
    @ValueSource(strings = {"true", "false"})
    void decode_SUCESS(final boolean useMaxPacketId) {
        final byte[] encoded = Bytes.concat(WELLFORMED_PUBCOMP_BEGIN, useMaxPacketId ? MAX_PACKET_ID : MIN_PACKET_ID);
        final ByteBuf byteBuf = channel.alloc().buffer();
        byteBuf.writeBytes(encoded);
        channel.writeInbound(byteBuf);
        final MqttPubComp pubComp = channel.readInbound();
        assertNotNull(pubComp);
        assertEquals(useMaxPacketId ? 65535 : 0, pubComp.getPacketIdentifier());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "2"})
    void decode_ERROR_CASES(final int errorcase) throws Exception {
        final byte[] encoded;
        switch (errorcase) {
            case 1:
                encoded = Bytes.concat(MALFORMED_PUBCOMP_BEGIN_WRONG_FLAGS, MAX_PACKET_ID);
                break;
            case 2:
                encoded =
                        Bytes.concat(MALFORMED_PUBCOMP_BEGIN_TOO_LONG_LENGTH, MIN_PACKET_ID, ENDING_TOO_LONG_MALFORMED);
                break;
            default:
                throw new Exception();
        }

        final ByteBuf byteBuf = channel.alloc().buffer();
        byteBuf.writeBytes(encoded);
        channel.writeInbound(byteBuf);
        final MqttPubComp pubComp = channel.readInbound();
        assertFalse(channel.isOpen());
        assertNull(pubComp);
    }
}
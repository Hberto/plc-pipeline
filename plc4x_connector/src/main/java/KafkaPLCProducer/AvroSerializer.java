package KafkaPLCProducer;

import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class AvroSerializer<T extends SpecificRecordBase> implements Serializer<T> {


    @Override
    public void configure(Map configs, boolean isKey) {
        // Nothing to do
    }

    @Override
    public byte[] serialize(String topic, T data) {
        try {
            byte[] result = null;

            if (data != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); //Byte Array for the encoder
                BinaryEncoder binaryEncoder = EncoderFactory.get().binaryEncoder(byteArrayOutputStream, null);

                DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(data.getSchema());
                datumWriter.write(data, binaryEncoder);

                binaryEncoder.flush();
                byteArrayOutputStream.close();

                result = byteArrayOutputStream.toByteArray();
            }
            return result;

        } catch (IOException e) {
            throw new SerializationException(
                        data + " topic " + topic,e
                );
            }
        }

        @Override
        public void close () {
            // Nothing to do
        }

}

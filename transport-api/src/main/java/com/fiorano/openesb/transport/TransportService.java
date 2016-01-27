package com.fiorano.openesb.transport;

public interface TransportService<E extends Port, M extends Message, PC extends PortConfiguration> {

    E enablePort(PC portConfiguration) throws Exception;

    void disablePort(PC portConfiguration) throws Exception;

    Consumer<M> createConsumer(E port, ConsumerConfiguration consumerConfiguration) throws Exception;

    Producer<M> createProducer(E port, ProducerConfiguration producerConfiguration) throws Exception;

    M createMessage() throws Exception;

}

package com.fiorano.openesb.transport;


public interface TransportService<E extends Port, M extends Message> {

    ConnectionProvider getConnectionProvider();

    E enablePort(PortConfiguration portConfiguration) throws Exception;

    void disablePort(PortConfiguration portConfiguration) throws Exception;

    Consumer<M> createConsumer(E port, ConsumerConfiguration consumerConfiguration) throws Exception;

    Producer<M> createProducer(E port, ProducerConfiguration producerConfiguration) throws Exception;

    M createMessage(MessageConfiguration config) throws Exception;

}

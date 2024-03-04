package com.withintegrity.jaxbspring;

import com.withintegrity.codegen.model.Itemtype;
import com.withintegrity.codegen.model.ObjectFactory;
import com.withintegrity.codegen.model.Shipordertype;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.UUID;

@SpringBootApplication
public class JaxbSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(JaxbSpringApplication.class, args);

    }

    @Bean
    JAXBContext context() throws JAXBException {
        return JAXBContext.newInstance("com.withintegrity.codegen.model");
    }

    @Bean
    Marshaller marshaller(JAXBContext context) throws JAXBException {
        return context.createMarshaller();
    }

    @Bean
    CommandLineRunner createXML(Marshaller marshaller) {
        return args -> {
            File file = Paths.get("target", "order-generated.xml").toFile();
            ObjectFactory objectFactory = new ObjectFactory();

            Itemtype itemtype = objectFactory.createItemtype();
            itemtype.setNote("Toothpaste");

            Shipordertype shipordertype = objectFactory.createShipordertype();
            shipordertype.getItem().add(itemtype);
            shipordertype.setOrderid("Ship this Order " + UUID.randomUUID());

            JAXBElement<Shipordertype> shiporder = objectFactory.createShiporder(shipordertype);

            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                marshaller.marshal(shiporder, byteArrayOutputStream);
                String printOutput = byteArrayOutputStream.toString(StandardCharsets.UTF_8);
                System.out.println(printOutput);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(byteArrayOutputStream.toByteArray());
            }
        };
    }


    @Component
    class Closed implements Closeable {
        @Override
        public void close() throws IOException {
            System.out.println("CLOSED!!!");
        }
    }


}

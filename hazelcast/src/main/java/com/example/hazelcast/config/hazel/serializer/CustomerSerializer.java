package com.example.hazelcast.config.hazel.serializer;

import com.example.hazelcast.dto.Customer;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;
import java.io.IOException;

public class CustomerSerializer implements StreamSerializer<Customer> {

  @Override
  public void write(ObjectDataOutput out, Customer object) throws IOException {
    out.writeUTF(object.getName());
    out.writeUTF(object.getLastName());
  }

  @Override
  public Customer read(ObjectDataInput in) throws IOException {
    return Customer.builder()
        .name(in.readUTF())
        .lastName(in.readUTF())
        .build();
  }

  @Override
  public int getTypeId() {
    return 1;
  }
}

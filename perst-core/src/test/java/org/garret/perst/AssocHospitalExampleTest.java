package org.garret.perst;

import java.util.List;

import org.garret.perst.assoc.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class AssocHospitalExampleTest {
    @Test
    public void hospitalQueriesAndUpdate() {
        Storage storage = StorageFactory.getInstance().createStorage();
        storage.open("AssocHospitalExampleTest.dbs", Storage.INFINITE_PAGE_POOL);
        AssocDB db = new AssocDB(storage);
        try {
            // Populate database
            ReadWriteTransaction t = db.startReadWriteTransaction();

            Item patient = t.createItem();
            t.link(patient, "class", "patient");
            t.link(patient, "name", "John Smith");
            t.link(patient, "age", 55);
            t.link(patient, "wight", 65.7);
            t.link(patient, "sex", "male");
            t.link(patient, "phone", "1234567");
            t.link(patient, "address", "123456, CA, Dummyngton, Outlook drive, 17");

            Item doctor = t.createItem();
            t.link(doctor, "class", "doctor");
            t.link(doctor, "name", "Robby Wood");
            t.link(doctor, "speciality", "therapeutist");

            Item angina = t.createItem();
            t.link(angina, "class", "disease");
            t.link(angina, "name", "angina");
            t.link(angina, "symptoms", "throat ache");
            t.link(angina, "symptoms", "high temperature");
            t.link(angina, "treatment", "milk&honey");

            Item flu = t.createItem();
            t.link(flu, "class", "disease");
            t.link(flu, "name", "flu");
            t.link(flu, "symptoms", "stomachache");
            t.link(flu, "symptoms", "high temperature");
            t.link(flu, "treatment", "theraflu");

            Item diagnosis = t.createItem();
            t.link(diagnosis, "class", "diagnosis");
            t.link(diagnosis, "disease", flu);
            t.link(diagnosis, "symptoms", "high temperature");
            t.link(diagnosis, "diagnosed-by", doctor);
            t.link(diagnosis, "date", "2010-09-23");
            t.link(patient, "diagnosis", diagnosis);

            t.commit();

            // Query database
            ReadOnlyTransaction r = db.startReadOnlyTransaction();
            List<Item> patients = r.find(
                Predicate.and(
                    Predicate.compare("age", Predicate.Compare.Operation.GreaterThan, 50),
                    Predicate.in("diagnosis",
                        Predicate.and(
                            Predicate.between("date", "2010-09-01", "2010-09-30"),
                            Predicate.in("disease",
                                Predicate.compare("name", Predicate.Compare.Operation.Equals, "flu")
                            )
                        )
                    )
                )
            ).toList();
            assertEquals(1, patients.size());
            assertEquals("John Smith", patients.get(0).getString("name"));

            Item[] diseases = r.find(
                Predicate.and(
                    Predicate.compare("class", Predicate.Compare.Operation.Equals, "disease"),
                    Predicate.compare("symptoms", Predicate.Compare.Operation.Equals, "high temperature")
                ),
                new OrderBy("name")
            );
            assertEquals(2, diseases.length);
            assertEquals("angina", diseases[0].getString("name"));
            assertEquals("flu", diseases[1].getString("name"));
            r.commit();

            // Update patient age
            t = db.startReadWriteTransaction();
            Item found = t.find(
                Predicate.and(
                    Predicate.compare("class", Predicate.Compare.Operation.Equals, "patient"),
                    Predicate.compare("name", Predicate.Compare.Operation.Equals, "John Smith")
                )
            ).first();
            t.update(found, "age", 56);
            t.commit();

            ReadOnlyTransaction check = db.startReadOnlyTransaction();
            Item updated = check.find(
                Predicate.compare("name", Predicate.Compare.Operation.Equals, "John Smith")
            ).first();
            assertEquals(56, updated.getNumber("age").intValue());
            check.commit();
        } finally {
            storage.close();
            try {
                new java.io.File("AssocHospitalExampleTest.dbs").delete();
            } catch (Exception ignore) {}
        }
    }
}

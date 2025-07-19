package in.ecomexpress.sruti.model.menifestdata;


import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

/**
 * Created by 63091 on 13-07-2019.
 */

public class ManifestAndShipment {
    @Embedded
    public Manifest_List manifest_list;

    @Relation(parentColumn = "manifestNo", entityColumn = "manifestNoInchild")
    public List<Shipment_Detail> shipment_details;
}

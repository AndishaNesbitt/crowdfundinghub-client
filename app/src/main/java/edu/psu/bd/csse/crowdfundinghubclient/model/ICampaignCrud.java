package edu.psu.bd.csse.crowdfundinghubclient.model;

import java.util.List;

/**
 * Created by Zach on 1/31/2016.
 */
public interface ICampaignCrud {

    public void addCampaign(Campaign campaign);
    public List<Campaign> getCampaigns(int section);

}

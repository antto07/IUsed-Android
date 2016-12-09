package com.iused.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iused.R;
import com.iused.adapters.DonatedByOthersAdapter;
import com.iused.bean.DonatedByOthersBean;
import com.iused.dialog.DonatedByOthersContactDialog;
import com.iused.main.ProductDetailsActivity_Donate;
import com.iused.utils.AsyncTaskListener;
import com.iused.utils.HttpAsync;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Antto on 05-10-2016.
 */
public class Dontated_By_Others_Fragment extends Fragment implements AsyncTaskListener{

    View view;
    private RecyclerView list_ordered_products=null;
    private HashMap<String, String> para = null;
    private AsyncTaskListener listener = null;
    public SharedPreferences mpref = null;
    private ArrayList<DonatedByOthersBean> donatedByOthersBeen= null;
    DonatedByOthersAdapter adapter;
    private ProgressDialog progressDialog= null;
    private TextView txt_no_products_donated_by_others=null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_donated_by_others, container, false);

        listener=Dontated_By_Others_Fragment.this;
        progressDialog= new ProgressDialog(getActivity());

        list_ordered_products= (RecyclerView) view.findViewById(R.id.list_donated_by_others);
        list_ordered_products.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        list_ordered_products.setLayoutManager(layoutManager);
        list_ordered_products.setNestedScrollingEnabled(false);

        txt_no_products_donated_by_others= (TextView) view.findViewById(R.id.txt_no_products_donated_by_others);

        mpref = getActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE);
        if (mpref != null)

            donatedByOthersBeen= new ArrayList<DonatedByOthersBean>();

        para = new HashMap<>();
        para.put("UserId", mpref.getString("user_id", ""));
        Log.e("para_get_products", para.toString());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        HttpAsync httpAsync1 = new HttpAsync(getActivity(), listener, "http://54.191.146.243:8088/GetDonations", para, 2, "donations");
        httpAsync1.execute();

        return view;
    }

    @Override
    public void onTaskCancelled(String data) {

    }

    @Override
    public void onTaskComplete(String result, String tag) {
        progressDialog.dismiss();
        if(result.equalsIgnoreCase("fail")){
            Toast.makeText(getActivity(),"Check your internet connection",Toast.LENGTH_SHORT).show();
        }
        else{
            if(tag.equalsIgnoreCase("donations")){
                JSONObject jsonObject = null;
                try {

                    jsonObject = new JSONObject(result);
                    if (jsonObject != null){
                        if(jsonObject.getString("errFlag").equalsIgnoreCase("0")){
                            JSONArray jsonsendarr = jsonObject.optJSONArray("OtherDonations");
                            if (jsonsendarr.length() > 0){
                                for (int i = 0; i < jsonsendarr.length(); i++){
                                    if (jsonObject != null){
                                        DonatedByOthersBean ordered_Products_Bean=new DonatedByOthersBean();
                                        JSONObject volumobject = jsonsendarr.getJSONObject(i);

                                        ordered_Products_Bean.setUsedFor(volumobject.getString("UsedFor"));
                                        ordered_Products_Bean.setPostedBy(volumobject.getString("PostedBy"));
                                        ordered_Products_Bean.setPostedByUserName(volumobject.getString("PostedByUserName"));
                                        ordered_Products_Bean.setPostedByPhone(volumobject.getString("PostedByPhone"));
                                        ordered_Products_Bean.setCondition(volumobject.getString("Condition"));
                                        ordered_Products_Bean.setProductName(volumobject.getString("ProductName"));
                                        ordered_Products_Bean.setPostedByImage(volumobject.getString("PostedByImage"));
                                        ordered_Products_Bean.setDistance(volumobject.getString("Distance"));
                                        ordered_Products_Bean.setImage(volumobject.getString("Image"));
                                        ordered_Products_Bean.setPrice(volumobject.getString("Price"));
                                        ordered_Products_Bean.setQtyRemaining(volumobject.getString("QtyRemaining"));
                                        ordered_Products_Bean.setFavorite(volumobject.getString("Favorite"));
                                        ordered_Products_Bean.setDescription(volumobject.getString("Description"));
                                        ordered_Products_Bean.setType(volumobject.getString("Type"));
                                        ordered_Products_Bean.setProductId(volumobject.getString("ProductId"));

                                        donatedByOthersBeen.add(ordered_Products_Bean);

                                    }
                                }

                                if(donatedByOthersBeen.size()>0){
                                    try {

                                        adapter = new DonatedByOthersAdapter(getActivity(), donatedByOthersBeen);
                                        list_ordered_products.setAdapter(adapter);
//                                        grid_item.setVisibility(View.VISIBLE);

                                    }catch (Exception e){

                                    }
                                }

                                adapter.SetOnItemClickListener(new DonatedByOthersAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
//                                        DonatedByOthersContactDialog donatedByOthersContactDialog=new DonatedByOthersContactDialog(getActivity(),donatedByOthersBeen.get(position).getPostedByUserName(),donatedByOthersBeen.get(position).getPostedByPhone());
//                                        donatedByOthersContactDialog.show();
                                        try {
                                            Intent intent = new Intent(getActivity(), ProductDetailsActivity_Donate.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.putExtra("image",donatedByOthersBeen.get(position).getImage());
                                            intent.putExtra("name", donatedByOthersBeen.get(position).getPostedByUserName());
                                            intent.putExtra("price", donatedByOthersBeen.get(position).getPrice());
                                            intent.putExtra("Condition",donatedByOthersBeen.get(position).getCondition());
                                            intent.putExtra("donor_image",donatedByOthersBeen.get(position).getPostedByImage());
                                            intent.putExtra("product_name", donatedByOthersBeen.get(position).getProductName());
                                            intent.putExtra("product_id", donatedByOthersBeen.get(position).getProductId());
                                            intent.putExtra("description", donatedByOthersBeen.get(position).getDescription());
                                            intent.putExtra("distance", donatedByOthersBeen.get(position).getDistance());
                                            intent.putExtra("qty_remaining", donatedByOthersBeen.get(position).getQtyRemaining());
                                            intent.putExtra("type", donatedByOthersBeen.get(position).getType());
                                            intent.putExtra("used_for", donatedByOthersBeen.get(position).getUsedFor());
                                            intent.putExtra("phone", donatedByOthersBeen.get(position).getPostedByPhone());
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                ImageView imageView = (ImageView) view.findViewById(R.id.img_product);
                                                ActivityOptionsCompat options = ActivityOptionsCompat.
                                                        makeSceneTransitionAnimation(getActivity(), imageView, "profile");
                                                startActivity(intent, options.toBundle());
                                            } else {
                                                startActivity(intent);
                                            }
                                        } catch (Exception e) {

                                        }

                                    }
                                });


                            }
                            else {
                                list_ordered_products.setVisibility(View.GONE);
                                txt_no_products_donated_by_others.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }
}

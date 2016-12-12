package com.iused.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.iused.R;
import com.iused.bean.MainProductsBean;
import com.iused.bean.WishListProductsBeanDonations;
import com.iused.bean.WishListProductsBeanNegotiable;
import com.iused.bean.WishListProductsBeanNonNegotiable;
import com.iused.fragments.FaqPagerAdapter;
import com.iused.utils.AsyncTaskListener;
import com.iused.utils.Constants;
import com.iused.utils.HttpAsync;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lenovo on 02/11/2016.
 */
public class WishlistActivity extends AppCompatActivity implements AsyncTaskListener {

    public SharedPreferences mpref = null;
    protected Context context = null;
    private PagerAdapter faq_adapter = null;
    private ViewPager faq_viewPager = null;
    private HashMap<String, String> para = null;
    private AsyncTaskListener listener = null;
    TabLayout tabLayout = null;
    private ProgressDialog progressDialog= null;

    public static ArrayList<MainProductsBean> wishlistProductsBeanNegotiable = null;
    public static ArrayList<MainProductsBean> wishlistProductsBeanNonNegotiable = null;
    public static ArrayList<MainProductsBean> wishlistProductsBeanDonations = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_wishlist);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search For Products");

        tabLayout = (TabLayout) findViewById(R.id.faqtab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Compete"));
        tabLayout.addTab(tabLayout.newTab().setText("Fixed Price"));
        tabLayout.addTab(tabLayout.newTab().setText("Donations"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        context = getApplicationContext();
        progressDialog= new ProgressDialog(WishlistActivity.this);

        faq_viewPager = (ViewPager) findViewById(R.id.faq_pager);


        mpref = getSharedPreferences("user_details", MODE_PRIVATE);
        if (mpref != null)
            listener = WishlistActivity.this;
        para = new HashMap<>();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);

        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchViewAndroidActionBar.clearFocus();
                Log.e("jkvchj", query);

                para = new HashMap<>();
                para.put("UserId", mpref.getString("user_id", ""));
                para.put("WishList", query);
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                HttpAsync httpAsync1 = new HttpAsync(getApplicationContext(), listener, Constants.BASE_URL+"UpdateWishList", para, 2, "products");
                httpAsync1.execute();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onTaskCancelled(String data) {

    }

    @Override
    public void onTaskComplete(String result, String tag) {

        progressDialog.dismiss();
        if (result.equalsIgnoreCase("fail")) {
            try {
                Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
            }catch (Exception e){

            }
        } else {
            if (tag.equalsIgnoreCase("products")) {
                JSONObject jsonObject = null;
//                mainProductsBean.clear();
//                mainProductsBean_non_negotiable.clear();

                try {

                    jsonObject = new JSONObject(result);
                    if (jsonObject != null) {
                        if (jsonObject.getString("errFlag").equalsIgnoreCase("0")) {

                            faq_adapter = new FaqPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());


                            JSONArray jsonsendarr = jsonObject.optJSONArray("Negotiable");
                            if (jsonsendarr.length() > 0) {
                                wishlistProductsBeanNegotiable = new ArrayList<MainProductsBean>();
                                for (int i = 0; i < jsonsendarr.length(); i++) {
                                    if (jsonObject != null) {

                                        MainProductsBean main_Products_Bean = new MainProductsBean();
                                        JSONObject volumobject = jsonsendarr.getJSONObject(i);

                                        main_Products_Bean.setUsedFor(volumobject.getString("UsedFor"));
                                        main_Products_Bean.setPostedBy(volumobject.getString("PostedBy"));
                                        main_Products_Bean.setProductName(volumobject.getString("ProductName"));
                                        main_Products_Bean.setDistance(volumobject.getString("Distance"));
                                        main_Products_Bean.setImage(volumobject.getString("Image"));
                                        main_Products_Bean.setPrice(volumobject.getString("Price"));
                                        main_Products_Bean.setQtyRemaining(volumobject.getString("QtyRemaining"));
                                        main_Products_Bean.setFavorite(volumobject.getString("Favorite"));
                                        main_Products_Bean.setDescription(volumobject.getString("Description"));
                                        main_Products_Bean.setType(volumobject.getString("Type"));
                                        main_Products_Bean.setProductId(volumobject.getString("ProductId"));

//                                        str_type_1 = volumobject.getString("Type");

                                        wishlistProductsBeanNegotiable.add(main_Products_Bean);


                                        faq_viewPager.setAdapter(faq_adapter);
                                        faq_viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                                        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                            @Override
                                            public void onTabSelected(TabLayout.Tab tab) {
                                                faq_viewPager.setCurrentItem(tab.getPosition());

                                            }

                                            @Override
                                            public void onTabUnselected(TabLayout.Tab tab) {

                                            }

                                            @Override
                                            public void onTabReselected(TabLayout.Tab tab) {

                                            }
                                        });

                                    }
                                }

//                                if (mainProductsBean.size() > 0) {
//                                    try {
//                                        Log.e("products", mainProductsBean.size()+" ");
//                                        adapter = new ProductMainAdapter(getActivity(), mainProductsBean);
//                                        grid_item.setAdapter(adapter);
////                                        grid_item.setVisibility(View.VISIBLE);
//
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }


//                                adapter.SetOnItemClickListener(new ProductMainAdapter.OnItemClickListener() {
//                                    @Override
//                                    public void onItemClick(View view, int position) {
//
//                                        try {
//                                            Intent intent = new Intent(getActivity(), ProductDetailsActivity_Negotiable.class);
//                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                            intent.putExtra("image", mainProductsBean.get(position).getImage());
//                                            intent.putExtra("name", mainProductsBean.get(position).getProductName());
//                                            intent.putExtra("price", mainProductsBean.get(position).getPrice());
//                                            intent.putExtra("product_name", mainProductsBean.get(position).getProductName());
//                                            intent.putExtra("product_id", mainProductsBean.get(position).getProductId());
//                                            intent.putExtra("description", mainProductsBean.get(position).getDescription());
//                                            intent.putExtra("distance", mainProductsBean.get(position).getDistance());
//                                            intent.putExtra("qty_remaining", mainProductsBean.get(position).getQtyRemaining());
//                                            intent.putExtra("type", mainProductsBean.get(position).getType());
//                                            intent.putExtra("used_for", mainProductsBean.get(position).getUsedFor());
//                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                                ImageView imageView = (ImageView) view.findViewById(R.id.img_product);
//                                                ActivityOptionsCompat options = ActivityOptionsCompat.
//                                                        makeSceneTransitionAnimation(getActivity(), imageView, "profile");
//                                                startActivity(intent, options.toBundle());
//                                            } else {
//                                                startActivity(intent);
//                                            }
//                                        } catch (Exception e) {
//
//                                        }
//
//                                    }
//                                });


                            }

                            JSONArray jsonsendarr1 = jsonObject.optJSONArray("NonNegotiable");
                            if (jsonsendarr1.length() > 0) {
                                wishlistProductsBeanNonNegotiable = new ArrayList<MainProductsBean>();
                                Log.e("non_neg_length", jsonsendarr1.length() + " ");
                                for (int i = 0; i < jsonsendarr1.length(); i++) {
                                    if (jsonObject != null) {
                                        MainProductsBean main_Products_Bean = new MainProductsBean();
                                        JSONObject volumobject = jsonsendarr1.getJSONObject(i);

                                        main_Products_Bean.setUsedFor(volumobject.getString("UsedFor"));
                                        main_Products_Bean.setPostedBy(volumobject.getString("PostedBy"));
                                        main_Products_Bean.setProductName(volumobject.getString("ProductName"));
                                        main_Products_Bean.setDistance(volumobject.getString("Distance"));
                                        main_Products_Bean.setImage(volumobject.getString("Image"));
                                        main_Products_Bean.setPrice(volumobject.getString("Price"));
                                        main_Products_Bean.setQtyRemaining(volumobject.getString("QtyRemaining"));
                                        main_Products_Bean.setFavorite(volumobject.getString("Favorite"));
                                        main_Products_Bean.setDescription(volumobject.getString("Description"));
                                        main_Products_Bean.setType(volumobject.getString("Type"));
                                        main_Products_Bean.setProductId(volumobject.getString("ProductId"));

//                                        str_type_2 = volumobject.getString("Type");

                                        wishlistProductsBeanNonNegotiable.add(main_Products_Bean);
//                                        mainProductsBean.add(main_Products_Bean);
                                    }
                                }

//                                if (mainProductsBean_non_negotiable.size() > 0) {
//                                    try {
//                                        Log.e("products_non", mainProductsBean_non_negotiable.size()+" ");
//                                        adapter_non_negotiable = new ProductMainAdapterNonNegotiable(getActivity(), mainProductsBean_non_negotiable);
//                                        grid_item_non_negotiable.setAdapter(adapter_non_negotiable);
////                                        grid_item_non_negotiable.setVisibility(View.VISIBLE);
//
//                                    } catch (Exception e) {
//                                    }
//                                }
//
//                                adapter_non_negotiable.SetOnItemClickListener(new ProductMainAdapterNonNegotiable.OnItemClickListener() {
//                                    @Override
//                                    public void onItemClick(View view, int position) {
//
//                                        try {
//                                            Intent intent = new Intent(getActivity(), ProductDetailsActivity_Non_Negotiable.class);
//                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                            intent.putExtra("image", mainProductsBean_non_negotiable.get(position).getImage());
//                                            intent.putExtra("name", mainProductsBean_non_negotiable.get(position).getProductName());
//                                            intent.putExtra("price", mainProductsBean_non_negotiable.get(position).getPrice());
//                                            intent.putExtra("product_name", mainProductsBean_non_negotiable.get(position).getProductName());
//                                            intent.putExtra("product_id", mainProductsBean_non_negotiable.get(position).getProductId());
//                                            intent.putExtra("description", mainProductsBean_non_negotiable.get(position).getDescription());
//                                            intent.putExtra("distance", mainProductsBean_non_negotiable.get(position).getDistance());
//                                            intent.putExtra("qty_remaining", mainProductsBean_non_negotiable.get(position).getQtyRemaining());
//                                            intent.putExtra("type", mainProductsBean_non_negotiable.get(position).getType());
//                                            intent.putExtra("used_for", mainProductsBean_non_negotiable.get(position).getUsedFor());
//                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                                ImageView imageView = (ImageView) view.findViewById(R.id.img_product);
//                                                ActivityOptionsCompat options = ActivityOptionsCompat.
//                                                        makeSceneTransitionAnimation(getActivity(), imageView, "profile");
//                                                startActivity(intent, options.toBundle());
//                                            } else {
//                                                startActivity(intent);
//                                            }
//                                        } catch (Exception e) {
//
//                                        }
//
//                                    }
//                                });


                            }

                            JSONArray jsonsendarr2 = jsonObject.optJSONArray("Donation");
                            if (jsonsendarr2.length() > 0) {
                                wishlistProductsBeanDonations = new ArrayList<MainProductsBean>();
                                Log.e("non_neg_length", jsonsendarr2.length() + " ");
                                for (int i = 0; i < jsonsendarr2.length(); i++) {
                                    if (jsonObject != null) {
                                        MainProductsBean main_Products_Bean = new MainProductsBean();
                                        JSONObject volumobject = jsonsendarr2.getJSONObject(i);

                                        main_Products_Bean.setUsedFor(volumobject.getString("UsedFor"));
                                        main_Products_Bean.setPostedBy(volumobject.getString("PostedBy"));
                                        main_Products_Bean.setProductName(volumobject.getString("ProductName"));
                                        main_Products_Bean.setDistance(volumobject.getString("Distance"));
                                        main_Products_Bean.setImage(volumobject.getString("Image"));
                                        main_Products_Bean.setPrice(volumobject.getString("Price"));
                                        main_Products_Bean.setQtyRemaining(volumobject.getString("QtyRemaining"));
                                        main_Products_Bean.setFavorite(volumobject.getString("Favorite"));
                                        main_Products_Bean.setDescription(volumobject.getString("Description"));
                                        main_Products_Bean.setType(volumobject.getString("Type"));
                                        main_Products_Bean.setProductId(volumobject.getString("ProductId"));

//                                        str_type_2 = volumobject.getString("Type");

                                        wishlistProductsBeanDonations.add(main_Products_Bean);
//                                        mainProductsBean.add(main_Products_Bean);
                                    }
                                }

//                                if (mainProductsBean_non_negotiable.size() > 0) {
//                                    try {
//                                        Log.e("products_non", mainProductsBean_non_negotiable.size()+" ");
//                                        adapter_non_negotiable = new ProductMainAdapterNonNegotiable(getActivity(), mainProductsBean_non_negotiable);
//                                        grid_item_non_negotiable.setAdapter(adapter_non_negotiable);
////                                        grid_item_non_negotiable.setVisibility(View.VISIBLE);
//
//                                    } catch (Exception e) {
//                                    }
//                                }
//
//                                adapter_non_negotiable.SetOnItemClickListener(new ProductMainAdapterNonNegotiable.OnItemClickListener() {
//                                    @Override
//                                    public void onItemClick(View view, int position) {
//
//                                        try {
//                                            Intent intent = new Intent(getActivity(), ProductDetailsActivity_Non_Negotiable.class);
//                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                            intent.putExtra("image", mainProductsBean_non_negotiable.get(position).getImage());
//                                            intent.putExtra("name", mainProductsBean_non_negotiable.get(position).getProductName());
//                                            intent.putExtra("price", mainProductsBean_non_negotiable.get(position).getPrice());
//                                            intent.putExtra("product_name", mainProductsBean_non_negotiable.get(position).getProductName());
//                                            intent.putExtra("product_id", mainProductsBean_non_negotiable.get(position).getProductId());
//                                            intent.putExtra("description", mainProductsBean_non_negotiable.get(position).getDescription());
//                                            intent.putExtra("distance", mainProductsBean_non_negotiable.get(position).getDistance());
//                                            intent.putExtra("qty_remaining", mainProductsBean_non_negotiable.get(position).getQtyRemaining());
//                                            intent.putExtra("type", mainProductsBean_non_negotiable.get(position).getType());
//                                            intent.putExtra("used_for", mainProductsBean_non_negotiable.get(position).getUsedFor());
//                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                                ImageView imageView = (ImageView) view.findViewById(R.id.img_product);
//                                                ActivityOptionsCompat options = ActivityOptionsCompat.
//                                                        makeSceneTransitionAnimation(getActivity(), imageView, "profile");
//                                                startActivity(intent, options.toBundle());
//                                            } else {
//                                                startActivity(intent);
//                                            }
//                                        } catch (Exception e) {
//
//                                        }
//
//                                    }
//                                });


                            }

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}

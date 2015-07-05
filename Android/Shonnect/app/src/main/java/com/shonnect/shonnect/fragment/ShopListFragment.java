package com.shonnect.shonnect.fragment;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shonnect.shonnect.R;
import com.shonnect.shonnect.ShonnectApplication;
import com.shonnect.shonnect.activity.ChatActivity;
import com.shonnect.shonnect.activity.ShopDetailActivity;
import com.shonnect.shonnect.dagger.component.ChatRestComponent;
import com.shonnect.shonnect.dagger.component.DaggerChatRestComponent;
import com.shonnect.shonnect.dagger.module.ApplicationModule;
import com.shonnect.shonnect.dagger.module.NetworkModule;
import com.shonnect.shonnect.dagger.module.RestChatModule;
import com.shonnect.shonnect.dagger.module.UserModule;
import com.shonnect.shonnect.model.Conversation;
import com.shonnect.shonnect.model.Shop;
import com.shonnect.shonnect.service.ChatRestService;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
public class ShopListFragment extends BaseListFragment<Shop> implements View.OnClickListener {

    private ShopAdapter shopAdapter;

    @Inject
    ChatRestService chatRestService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ChatRestComponent chatRestComponent = DaggerChatRestComponent.builder()
                .restChatModule(new RestChatModule())
                .applicationModule(new ApplicationModule(getActivity().getApplicationContext()))
                .networkModule(new NetworkModule())
                .userModule(new UserModule())
                .build();
        chatRestComponent.inject(this);
        shopAdapter = provideAdapter();
        rvList.setAdapter(shopAdapter);
    }

    @Override
    public void onRefresh() {
        List<Shop> shops = createShopList();
        updateAdapter(shops);
    }

    @Override
    public void onClick(View v) {
        Shop shop = (Shop) v.getTag();
        if (shop != null) {
//            createConversation(shop.getId());
            Intent showShopDetailIntent = ShopDetailActivity.getLaunchIntent(getActivity(), shop);
            startActivity(showShopDetailIntent);
        }
    }

    private void createConversation(String targetUserId) {
        chatRestService.createNewConversation(targetUserId,
                new ChatRestService.OnConversationCreatedListener() {
                    @Override
                    public void onConversationCreated(Conversation newConversation, Exception ex) {
                        if (ex != null) {
                            Toast.makeText(getActivity(), "Cannot connect to server. Please try again",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            goToChat(newConversation);
                        }
                    }
                });
    }

    private void goToChat(Conversation conversation) {
        Intent goToChatPage = ChatActivity.getLaunchIntent(getActivity(), conversation);
        startActivity(goToChatPage);
    }

    @Override
    protected void updateAdapter(List<Shop> data) {
        hideLoading();
        if (shopAdapter != null) {
            shopAdapter.updateShops(data);
        }
    }

    public List<Shop> createShopList() {
        List<Shop> shops = new ArrayList<>();
        String userId = "5597f05de5496b3cc2c91057";
        String url = "http://orangefashion.vn";
        shops.add(new Shop(userId, "Trung Nguyen Coffee Shop", "https://thehungriestpanda.files.wordpress.com/2012/09/vietnam-hcmc7.jpg",
                url));
        shops.add(new Shop(userId, "The Body Shop", "http://www.nationalrail.co.uk/SME/html/NRE_EUS/images/photos/800/o1809-0000100.jpg",
                url));
        shops.add(new Shop(userId, "Charles And Keith", "http://www.bitexcofinancialtower.com/wp-content/uploads/2013/09/charles.jpg",
                url));
        shops.add(new Shop(userId, "Bradley's Book Outlet", "http://www.bradleysbookoutlet.com/wp-content/uploads/2013/07/altoona.jpg",
                url));
        shops.add(new Shop(userId, "Flower Box", "https://traveltoeat.com/wp-content/uploads/2013/05/wpid-Photo-May-18-2013-627-AM1.jpg",
                url));
        shops.add(new Shop(userId, "Kaoshiung",
                "http://www.scandinavian-designers.com/EN/wp-content/uploads/2012/11/Kaohsiung-THSR-Shop-deisgnOne-Town-One-ProductOTOP%E9%AB%98%E9%9B%84%E5%B7%A6%E7%87%9F%E9%AB%98%E9%90%B5%E7%AB%99%E5%B0%88%E6%AB%83%E8%A8%AD%E8%A8%88%E5%8F%B0%E7%81%A3%E5%9C%B0%E6%96%B9%E7%89%B9%E8%89%B2%E7%94%A2%E5%93%81%E9%A4%A8OTOP-02.jpg",
                url));

        return shops;
    }

    @Override
    protected ShopAdapter provideAdapter() {
        return new ShopAdapter(createShopList(), LayoutInflater.from(getActivity()), this);
    }

    public static class ShopViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivShopBanner;
        private TextView tvShopName;
        private DisplayImageOptions options;
        public ShopViewHolder(View itemView) {
            super(itemView);
            ivShopBanner = (ImageView) itemView.findViewById(R.id.iv_shop_banner);
            tvShopName = (TextView) itemView.findViewById(R.id.tv_shop_name);
            options = ShonnectApplication.getDefaultDisplayOptionsBuilder().build();
        }

        public void bind(Shop shop, View.OnClickListener onShopClickedListener) {
            ImageLoader.getInstance().displayImage(shop.getBannerImage(), ivShopBanner, options);
            tvShopName.setText(shop.getShopName());
            itemView.setTag(shop);
            itemView.setOnClickListener(onShopClickedListener);
        }
    }

    private static class ShopAdapter extends RecyclerView.Adapter<ShopViewHolder> {

        private List<Shop> shops;
        private LayoutInflater layoutInflater;
        private View.OnClickListener onShopClickListener;

        public ShopAdapter(List<Shop> shops, LayoutInflater layoutInflater, View.OnClickListener onShopClickListener) {
            this.shops = shops;
            this.layoutInflater = layoutInflater;
            this.onShopClickListener = onShopClickListener;
        }

        @Override
        public ShopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ShopViewHolder(layoutInflater.inflate(R.layout.item_shop_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(ShopViewHolder holder, int position) {
            holder.bind(shops.get(position), onShopClickListener);
        }

        public void updateShops(List<Shop> data) {
            this.shops = new ArrayList<>(data);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return shops != null ? shops.size() : 0;
        }

    }

}

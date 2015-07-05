package com.shonnect.shonnect.fragment;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shonnect.shonnect.R;
import com.shonnect.shonnect.ShonnectApplication;
import com.shonnect.shonnect.activity.ChatActivity;
import com.shonnect.shonnect.control.CircleBitmapDisplayer;
import com.shonnect.shonnect.dagger.component.ChatRestComponent;
import com.shonnect.shonnect.dagger.component.DaggerChatRestComponent;
import com.shonnect.shonnect.dagger.module.ApplicationModule;
import com.shonnect.shonnect.dagger.module.NetworkModule;
import com.shonnect.shonnect.dagger.module.RestChatModule;
import com.shonnect.shonnect.dagger.module.UserModule;
import com.shonnect.shonnect.listener.OnConversationClickListener;
import com.shonnect.shonnect.manager.UserManager;
import com.shonnect.shonnect.model.ChatMessage;
import com.shonnect.shonnect.model.Conversation;
import com.shonnect.shonnect.model.User;
import com.shonnect.shonnect.service.ChatRestService;

import android.content.Context;
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
public class ConversationListFragment extends BaseListFragment<Conversation> {
    @Inject
    UserManager userManager;
    @Inject
    ChatRestService chatRestService;

    private ConversationListAdapter conversationListAdapter = null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ChatRestComponent chatRestComponent = DaggerChatRestComponent.builder()
                .restChatModule(new RestChatModule())
                .applicationModule(new ApplicationModule(getActivity().getApplicationContext()))
                .networkModule(new NetworkModule())
                .userModule(new UserModule())
                .build();
        chatRestComponent.inject(this);
        rvList.setAdapter(provideAdapter());
    }

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
    public void onResume() {
        super.onResume();
        if (conversationListAdapter == null || conversationListAdapter.getItemCount() == 0) {
            showLoading();
        }
        getData();
    }

    private void getData() {
        chatRestService.getConversationList(new ChatRestService.OnConversationListRetrievedListener() {
            @Override
            public void onConversationListRetrieved(List<Conversation> conversationList, Exception ex) {
                hideLoading();
                if (ex != null) {
                    ex.printStackTrace();
                    showError();
                } else {
                    updateAdapter(conversationList);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        showLoading();
        getData();
    }

    private void showError() {
        if (getActivity() != null && !isRemoving()) {
            Toast.makeText(getActivity(), "There's error getting your data, please try again", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected RecyclerView.Adapter<ConversationVH> provideAdapter() {
        User currentUser = userManager.getCurrentUser();
        String currentUserId = null;
        if (currentUser != null) {
            currentUserId = currentUser.getId();
        }
        conversationListAdapter = new ConversationListAdapter(getActivity(), currentUserId,
                new OnConversationClickListener() {
                    @Override
                    public void onConversationClicked(View v, Conversation conversation) {
                        createConversation(conversation);
                    }
                });
        return conversationListAdapter;
    }

    private void createConversation(Conversation conversation) {
        chatRestService.createNewConversation(
                conversation.getRealTargetUser(userManager.getCurrentUser().getId()).getId(),
                new ChatRestService.OnConversationCreatedListener() {
                    @Override
                    public void onConversationCreated(Conversation newConversation, Exception ex) {
                        if (ex != null) {
                            Toast.makeText(getActivity(), "Cannot connect to server. Please try again", Toast.LENGTH_LONG).show();
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
    protected void updateAdapter(List<Conversation> data) {
        if (conversationListAdapter != null) {
            List<Conversation> newData = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                newData.addAll(data);
            }
            conversationListAdapter.setData(newData);
        }
    }

    private static class ConversationListAdapter extends RecyclerView.Adapter<ConversationVH> {
        private Context context;
        private String currentUserId;
        private List<Conversation> data;
        private OnConversationClickListener onConversationClickListener;

        private ConversationListAdapter(Context context, String currentUserId,
                OnConversationClickListener onConversationClickListener) {
            this.context = context;
            this.currentUserId = currentUserId;
            this.onConversationClickListener = onConversationClickListener;
        }

        @Override
        public ConversationVH onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            return new ConversationVH(layoutInflater.inflate(R.layout.item_conversation_layout, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(ConversationVH conversationVH, int position) {
            conversationVH.bind(data.get(position), currentUserId, onConversationClickListener);
        }

        @Override
        public int getItemCount() {
            return data != null ? data.size() : 0;
        }

        public void setData(List<Conversation> data) {
            this.data = data;
            notifyDataSetChanged();
        }
    }

    private static class ConversationVH extends RecyclerView.ViewHolder {
        private TextView tvConversationName;
        private TextView tvConversationTime;
        private TextView tvConversationContent;
        private ImageView ivConversationImage;
        private DisplayImageOptions displayImageOptions;

        public ConversationVH(View itemView) {
            super(itemView);
            tvConversationContent = (TextView) itemView.findViewById(R.id.tv_conversation_content);
            tvConversationName = (TextView) itemView.findViewById(R.id.tv_conversation_name);
            tvConversationTime = (TextView) itemView.findViewById(R.id.tv_conversation_time);
            ivConversationImage = (ImageView) itemView.findViewById(R.id.iv_conversation_avatar);
            float circleRadius = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.circle_avatar_width) / 2.0f;
            displayImageOptions = ShonnectApplication.getDefaultDisplayOptionsBuilder()
                    .displayer(new CircleBitmapDisplayer((int) circleRadius))
                    .build();
        }

        public void bind(final Conversation conversation, String currentUserId,
                final OnConversationClickListener onConversationClickListener) {
            ChatMessage latestMessage = conversation.getMessages();
            if (latestMessage != null) {
                tvConversationContent.setText(latestMessage.getMessage());
                tvConversationTime.setText(latestMessage.getReadableTime());
            }
            User realTargetUser = conversation.getRealTargetUser(currentUserId);
            if (realTargetUser != null) {
                tvConversationName.setText(realTargetUser.getName());

                ImageLoader.getInstance().displayImage(realTargetUser.getAvatar(), ivConversationImage,
                        displayImageOptions);
            } else {
                tvConversationName.setText("");
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onConversationClickListener != null) {
                        onConversationClickListener.onConversationClicked(itemView, conversation);
                    }
                }
            });
        }
    }
}

package com.shonnect.shonnect.activity;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shonnect.shonnect.R;
import com.shonnect.shonnect.ShonnectApplication;
import com.shonnect.shonnect.control.BaseListingFragment;
import com.shonnect.shonnect.control.CircleBitmapDisplayer;
import com.shonnect.shonnect.dagger.component.ChatRestComponent;
import com.shonnect.shonnect.dagger.component.DaggerChatRestComponent;
import com.shonnect.shonnect.dagger.module.RestChatModule;
import com.shonnect.shonnect.dagger.module.UserModule;
import com.shonnect.shonnect.manager.UserManager;
import com.shonnect.shonnect.model.ChatMessage;
import com.shonnect.shonnect.model.Conversation;
import com.shonnect.shonnect.model.User;
import com.shonnect.shonnect.service.ChatRestService;
import com.shonnect.shonnect.service.ChatService;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
public class ChatActivity extends BaseChatRelatedActivity implements ChatService.ChatEventListener,
        View.OnClickListener {

    public static final String EXTRA_CONVERSATION = "extra-conversation-id";
    @Inject
    UserManager userManager;
    @Inject
    ChatRestService chatRestService;

    private RecyclerView rvChatList;
    private ChatAdapter chatAdapter;
    private Toolbar toolbar;
    private EditText etChat;
    private ImageView btFollow;
    private User currentUser;
    private User realTargetUser;
    private Conversation conversation;


    public static Intent getLaunchIntent(Context context, Conversation conversation) {
        Intent launchIntent = new Intent(context, ChatActivity.class);
        launchIntent.putExtra(EXTRA_CONVERSATION, conversation);
        return launchIntent;
    }

    @Override
    public void onChatJoined(final List<ChatMessage> messages) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                etChat.setEnabled(true);
                onChatMessageReceived(messages);
            }
        });
    }

    @Override
    public void onChatMessageReceived(final List<ChatMessage> chatMessageList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (chatAdapter == null) {
                    chatAdapter = new ChatAdapter(userManager.getCurrentUser());
                    rvChatList.setAdapter(chatAdapter);
                }
                chatAdapter.addChatMessages(chatMessageList);
                rvChatList.scrollToPosition(chatAdapter.getItemCount() - 1);
            }
        });

    }

    @Override
    public void onChatDisconnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                etChat.setEnabled(false);
                Toast.makeText(ChatActivity.this, "Reconnecting...", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onChatConnected() {
        chatService.initChatWithId(conversation.getId());
    }

    @Override
    public void onClick(View v) {
        String targetUserId = conversation.getRealTargetUser(userManager.getCurrentUser().getId()).getId();
        showLoadingDialog();

        if (conversation.isFollowed()) {
            chatRestService.unFollowUser(targetUserId, new ChatRestService.OnFollowListener() {
                @Override
                public void onFollowedUser(boolean isSuccess) {
                    hideLoadingDialog();
                    conversation.setIsFollowed(!isSuccess);
                    btFollow.setSelected(!isSuccess);
                }
            });
        } else {
            chatRestService.followUser(targetUserId,
                    new ChatRestService.OnFollowListener() {
                        @Override
                        public void onFollowedUser(boolean isSuccess) {
                            hideLoadingDialog();
                            btFollow.setSelected(isSuccess);
                            conversation.setIsFollowed(isSuccess);
                        }
                    });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        injectDependencies();
        currentUser = userManager.getCurrentUser();
        conversation = (Conversation) getIntent().getSerializableExtra(EXTRA_CONVERSATION);
        realTargetUser = conversation.getRealTargetUser(currentUser.getId());
        rvChatList = (RecyclerView) findViewById(R.id.rv_chat_list);
        btFollow = (ImageView) findViewById(R.id.bt_follow);
        btFollow.setOnClickListener(this);
        btFollow.setSelected(conversation.isFollowed());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        rvChatList.setLayoutManager(layoutManager);
        rvChatList.addItemDecoration(new BaseListingFragment.ListingSimpleDividerDecoration(15));
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        etChat = (EditText) findViewById(R.id.et_chat);
        etChat.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    if (chatService != null) {
                        ChatMessage chatMessage = new ChatMessage();
                        CharSequence text = v.getText();
                        if (!TextUtils.isEmpty(text)) {
                            chatMessage.setMessage(text.toString());
                            chatMessage.setUser(currentUser);
                            etChat.setText("");
                            chatService.sendChat(chatMessage);
                            return true;
                        }
                    }
                }
                return false;
            }
        });
        User targetUser = conversation.getRealTargetUser(userManager.getCurrentUser().getId());
        if (targetUser != null) {
            toolbar.setTitle(targetUser.getName());
        }
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        displayChatList(conversation.getChatMessages());
    }

    private void injectDependencies() {
        ChatRestComponent chatRestComponent = DaggerChatRestComponent.builder()
                .userModule(new UserModule())
                .applicationModule(getApplicatioModule())
                .networkModule(getNetworkModule())
                .restChatModule(new RestChatModule())
                .build();
        chatRestComponent.inject(this);
    }

    private void displayChatList(List<ChatMessage> chatMessageList) {
        if (chatAdapter == null) {
            chatAdapter = new ChatAdapter(currentUser);
            rvChatList.setAdapter(chatAdapter);
        }
        chatAdapter.setChatHistory(chatMessageList);
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        chatService.setOnChatEventListener(this);
        chatService.initChatWithId(conversation.getId());
    }

    @Override
    protected void onStop() {
        chatService.setOnChatEventListener(null);
        super.onStop();
    }

    private static class ChatVH extends RecyclerView.ViewHolder {
        private ImageView ivChatAvatar;
        private TextView tvChatContent;
        private DisplayImageOptions opts;
        private boolean isMyChat;

        public ChatVH(View itemView, boolean isMyChat) {
            super(itemView);
            this.isMyChat = isMyChat;
            ivChatAvatar = (ImageView) itemView.findViewById(R.id.iv_chat_avatar);
            tvChatContent = (TextView) itemView.findViewById(R.id.tv_chat_message);
            int radius = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.chat_avatar_dimen) / 2;
            opts = ShonnectApplication.getDefaultDisplayOptionsBuilder()
                    .displayer(new CircleBitmapDisplayer(radius))
                    .build();
        }

        public void bind(ChatMessage chatMessage) {
            if (!isMyChat) {
                ImageLoader.getInstance().displayImage(chatMessage.getUser().getAvatar(), ivChatAvatar, opts);
            }
            tvChatContent.setText(chatMessage.getMessage());

        }
    }

    private static class ChatAdapter extends RecyclerView.Adapter<ChatVH> {

        private static final int TYPE_MY_CHAT = 0x1;
        private static final int TYPE_OTHER_CHAT = 0x0;
        private List<ChatMessage> chatHistory;
        private User currentUser;
        private final Object lock = new Object();

        public ChatAdapter(User currentUser) {
            this.currentUser = currentUser;
        }

        @Override
        public ChatVH onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            if (viewType == TYPE_MY_CHAT) {
                return new ChatVH(layoutInflater.inflate(R.layout.item_my_chat_message, parent, false), true);
            } else {
                return new ChatVH(layoutInflater.inflate(R.layout.item_other_chat_message, parent, false), false);
            }
        }

        @Override
        public int getItemViewType(int position) {
            ChatMessage chatMessage = chatHistory.get(position);
            if (chatMessage.getUser() == null || chatMessage.getUser().equals(currentUser)) {
                return TYPE_MY_CHAT;
            } else {
                return TYPE_OTHER_CHAT;
            }
        }

        @Override
        public void onBindViewHolder(ChatVH holder, int position) {
            holder.bind(chatHistory.get(position));
        }

        @Override
        public int getItemCount() {
            return chatHistory != null ? chatHistory.size() : 0;
        }

        public void addChatMessages(List<ChatMessage> chatMessages) {
            synchronized (lock) {
                if (chatHistory != null) {
                    for (ChatMessage chatMessage : chatMessages) {
                        int chatIdx = chatHistory.indexOf(chatMessage);
                        if (chatIdx < 0) {
                            chatHistory.add(chatMessage);
                        }
                    }
                } else {
                    chatHistory = new ArrayList<>(chatMessages);
                }
                Collections.sort(chatHistory, new ChatComparator());
            }
            notifyDataSetChanged();
        }

        public void setChatHistory(List<ChatMessage> chatHistory) {
            synchronized (lock) {
                this.chatHistory = new ArrayList<>(chatHistory);
                Collections.sort(this.chatHistory, new ChatComparator());
            }
            notifyDataSetChanged();
        }
    }

    private static class ChatComparator implements Comparator<ChatMessage> {

        @Override
        public int compare(ChatMessage lhs, ChatMessage rhs) {
            if (lhs != null && rhs != null) {
                return lhs.getTimestamp() < rhs.getTimestamp() ? -1 : (lhs.getTimestamp() == rhs.getTimestamp() ? 0 : 1);
            }
            return 0;
        }
    }
 }

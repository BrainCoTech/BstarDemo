package tech.brainco.bstardemo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.annotations.NonNull;
import tech.brainco.bstardemo.databinding.ActivityConnectedDevicesBinding;
import tech.brainco.bstardemo.databinding.ItemDevicesBinding;
import tech.brainco.bstarsdk.core.BstarDevice;
import tech.brainco.bstarsdk.core.BstarSDK;
import tech.brainco.bstarsdk.core.Completable;

public class ConnectedDevicesActivity extends AppCompatActivity {

    public final static String KEY_DEVICES = "devices";
    private ActivityConnectedDevicesBinding binding;
    private RecyclerView.Adapter mAdapter;
    private List<BstarDevice> connectedDevices;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConnectedDevicesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.rv.setLayoutManager(new LinearLayoutManager(this));
        connectedDevices = BstarSDK.getDevices();
        BstarSDK.setBstarDevicesListener(result -> {
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        });
        mAdapter = new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ItemDevicesBinding itemBinding = ItemDevicesBinding.inflate(getLayoutInflater(), parent, false);
                itemBinding.getRoot().setTag(itemBinding);
                return new RecyclerView.ViewHolder(itemBinding.getRoot()) {
                };
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ItemDevicesBinding binding = (ItemDevicesBinding) holder.itemView.getTag();
                binding.tv.setText(connectedDevices.get(position).getId());
                BstarDevice device = connectedDevices.get(position);
                device.setAttentionListener(result -> {
                    if (holder.getAdapterPosition() == position) {
                        binding.tv2.setText("attention " + result);
                    }
                });
                binding.tv3.setText("contacted " + device.getContacted());
                device.setContactStateChangeListener(result -> {
                    if (holder.getAdapterPosition() == position) {
                        binding.tv3.setText("contacted " + result);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return connectedDevices.size();
            }
        };
        binding.rv.setAdapter(mAdapter);
        binding.btnStart.setOnClickListener(v -> BstarSDK.startEEG(new Completable() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onError(@NotNull Throwable t) {

            }
        }));
        binding.btnStop.setOnClickListener(v -> BstarSDK.stopEEG(new Completable() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onError(@NotNull Throwable t) {

            }
        }));
    }
}

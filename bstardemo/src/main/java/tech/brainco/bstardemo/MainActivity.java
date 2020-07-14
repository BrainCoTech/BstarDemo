package tech.brainco.bstardemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tech.brainco.bstardemo.databinding.ActivityMainBinding;
import tech.brainco.bstardemo.databinding.ItemMainBinding;
import tech.brainco.bstarsdk.core.BstarSDK;
import tech.brainco.bstarsdk.core.Result;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private RecyclerView.Adapter<RecyclerView.ViewHolder> mAdapter;
    private List<String> macList = new ArrayList<>();
    private Set<Integer> checkList = new HashSet<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.rv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ItemMainBinding itemBinding = ItemMainBinding.inflate(getLayoutInflater(), parent, false);
                itemBinding.getRoot().setTag(itemBinding);
                return new RecyclerView.ViewHolder(itemBinding.getRoot()) {
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ItemMainBinding binding = (ItemMainBinding) holder.itemView.getTag();
                binding.cb.setText(macList.get(position));
                binding.cb.setChecked(checkList.contains(position));
                binding.cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                            int pos = holder.getAdapterPosition();
                            if (isChecked) {
                                checkList.add(pos);
                            } else {
                                checkList.remove(pos);
                            }
                        }
                );
            }

            @Override
            public int getItemCount() {
                return macList.size();
            }
        };
        binding.rv.setAdapter(mAdapter);
        binding.btnConnect.setOnClickListener(v -> {
            if (checkList.isEmpty()) {
                Toast.makeText(this, "请选中设备", Toast.LENGTH_SHORT).show();
            } else {
                List<String> selectedMacList = new ArrayList<>();
                for (int i : checkList) {
                    selectedMacList.add(macList.get(i));
                }
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.show();
                BstarSDK.setHubConfig(selectedMacList, new Result<List<String>>() {
                    @Override
                    public void onResult(List<String> result) {
                        Timber.d("connect result %s", result);
                        dialog.dismiss();
                        Intent intent = new Intent(MainActivity.this, ConnectedDevicesActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(@NotNull Throwable t) {
                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });
        binding.btnDevices.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ConnectedDevicesActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.show();
            BstarSDK.scanDevices(3, new Result<List<String>>() {
                @Override
                public void onResult(List<String> result) {
                    macList.clear();
                    macList.addAll(result);
                    binding.btnConnect.setVisibility(macList.isEmpty() ? View.GONE : View.VISIBLE);
                    mAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }

                @Override
                public void onError(@NotNull Throwable t) {
                    Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

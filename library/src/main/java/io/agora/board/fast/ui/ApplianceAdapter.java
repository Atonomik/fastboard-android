package io.agora.board.fast.ui;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.agora.board.fast.library.R;
import io.agora.board.fast.model.ApplianceItem;
import io.agora.board.fast.model.FastStyle;

/**
 * @author fenglibin
 */
public class ApplianceAdapter extends HolderCacheAdapter<ApplianceAdapter.ViewHolder> {
    private List<ApplianceItem> appliances;

    private ApplianceItem curAppliance;
    private ColorStateList iconColor;
    private OnApplianceClickListener onApplianceClickListener;

    public ApplianceAdapter(List<ApplianceItem> appliances) {
        this.appliances = appliances;
    }

    @NonNull
    @Override
    public ViewHolder onChildCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tools_appliance, parent, false);
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ApplianceItem item = appliances.get(position);

        holder.appliance.setImageResource(item.icon);
        holder.appliance.setImageTintList(iconColor);
        holder.appliance.setSelected(item == curAppliance);
        holder.itemView.setOnClickListener(v -> {
            if (item != ApplianceItem.OTHER_CLEAR) {
                curAppliance = item;
            }
            if (onApplianceClickListener != null) {
                onApplianceClickListener.onApplianceClick(item);
            }
            updateSelected();
        });
    }

    @Override
    public int getItemCount() {
        return appliances.size();
    }

    public void setOnApplianceClickListener(OnApplianceClickListener onApplianceClickListener) {
        this.onApplianceClickListener = onApplianceClickListener;
    }

    public void setApplianceItem(ApplianceItem appliance) {
        curAppliance = appliance;
        updateSelected();
    }

    public void setStyle(FastStyle style) {
        iconColor = ResourceFetcher.get().getIconColor(style.isDarkMode());
        for (ViewHolder viewHolder : holdersCache) {
            viewHolder.appliance.setBackground(ResourceFetcher.get().createApplianceBackground(style.isDarkMode()));
        }
        updateSelected();
    }

    private void updateSelected() {
        for (ViewHolder viewHolder : holdersCache) {
            int position = viewHolder.getAdapterPosition();
            ApplianceItem item = appliances.get(position);
            viewHolder.appliance.setImageTintList(iconColor);
            viewHolder.appliance.setSelected(item == curAppliance);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView appliance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appliance = itemView.findViewById(R.id.appliance);
        }
    }

    public interface OnApplianceClickListener {
        void onApplianceClick(ApplianceItem item);
    }
}

package io.agora.board.fast.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.herewhite.sdk.domain.MemberState;
import com.herewhite.sdk.domain.ShapeType;

import io.agora.board.fast.BoardStateObserver;
import io.agora.board.fast.FastRoom;
import io.agora.board.fast.FastSdk;
import io.agora.board.fast.library.R;
import io.agora.board.fast.model.ApplianceItem;
import io.agora.board.fast.model.FastStyle;
import io.agora.board.fast.model.RedoUndoCount;

public class FastRoomController extends RelativeLayout implements RoomController, BoardStateObserver {
    private ToolButton toolButton;
    private ToolsLayout toolsLayout;

    private SubToolButton subToolButton;
    private SubToolsLayout subToolsLayout;
    private RedoUndoLayout redoUndoLayout;
    private ScenesLayout scenesLayout;

    private View overlayView;

    private FastSdk fastSdk;
    private FastRoom fastRoom;

    public FastRoomController(Context context) {
        this(context, null);
    }

    public FastRoomController(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FastRoomController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupView(context);
    }

    private void setupView(Context context) {
        View root = LayoutInflater.from(context).inflate(R.layout.layout_fast_room_controller, this, true);

        overlayView = root.findViewById(R.id.overlay_handle_view);
        toolButton = root.findViewById(R.id.tool_button);
        subToolButton = root.findViewById(R.id.sub_tool_button);
        toolsLayout = root.findViewById(R.id.tools_layout);
        toolsLayout.setOnApplianceClickListener(item -> {
            if (item != ApplianceItem.OTHER_CLEAR) {
                toolButton.setApplianceItem(item);
                subToolButton.setApplianceItem(item);
            }

            hideAllOverlay();

            if (item == ApplianceItem.OTHER_CLEAR) {
                fastRoom.cleanScene();
            } else {
                fastRoom.setAppliance(item.appliance);
            }
        });

        subToolsLayout = root.findViewById(R.id.sub_tools_layout);
        subToolsLayout.setOnColorClickListener(color -> {
            subToolButton.setColor(color);
            subToolsLayout.setColor(color);

            hideAllOverlay();

            fastRoom.setColor(color);
        });

        subToolsLayout.setOnStrokeChangedListener(width ->
                fastRoom.setStokeWidth(width)
        );

        toolButton.setOnClickListener(v -> {
            boolean target = !toolButton.isSelected();
            toolButton.setSelected(target);
            toolsLayout.setShown(target);

            subToolButton.setSelected(false);
            subToolsLayout.setShown(false);

            updateOverlay();
        });

        subToolButton.setOnSubToolClickListener(new SubToolButton.OnSubToolClickListener() {
            @Override
            public void onDeleteClick() {
                fastRoom.getRoom().deleteOperation();
            }

            @Override
            public void onColorClick() {
                boolean target = !subToolButton.isSelected();
                subToolButton.setSelected(target);
                subToolsLayout.setShown(target);

                toolButton.setSelected(false);
                toolsLayout.setShown(false);

                updateOverlay();
            }
        });

        redoUndoLayout = root.findViewById(R.id.redo_undo_layout);
        redoUndoLayout.setOnRedoUndoClickListener(new RedoUndoLayout.OnRedoUndoClickListener() {
            @Override
            public void onUndoClick() {
                fastRoom.getRoom().undo();
            }

            @Override
            public void onRedoClick() {
                fastRoom.getRoom().redo();
            }
        });

        scenesLayout = root.findViewById(R.id.scenes_layout);

        overlayView.setOnClickListener(v ->
                hideAllOverlay()
        );
    }

    private void hideAllOverlay() {
        if (toolButton.isSelected()) {
            toolButton.setSelected(false);
            toolsLayout.setShown(false);
        }

        if (subToolButton.isSelected()) {
            subToolButton.setSelected(false);
            subToolsLayout.setShown(false);
        }

        overlayView.setVisibility(GONE);
    }

    private void updateOverlay() {
        boolean showOverlay = toolButton.isSelected() || subToolButton.isSelected();
        overlayView.setVisibility(showOverlay ? VISIBLE : GONE);
    }

    @Override
    public void onMemberStateChanged(MemberState memberState) {
        updateAppliance(memberState.getCurrentApplianceName(), memberState.getShapeType());
        updateStroke(memberState.getStrokeColor(), memberState.getStrokeWidth());
    }

    private void updateAppliance(String appliance, ShapeType shapeType) {
        ApplianceItem item = ApplianceItem.of(appliance);
        toolButton.setApplianceItem(item);
        toolsLayout.setApplianceItem(item);
        subToolButton.setApplianceItem(item);
    }

    private void updateStroke(int[] strokeColor, double strokeWidth) {
        int color = Color.rgb(strokeColor[0], strokeColor[1], strokeColor[2]);
        subToolButton.setColor(color);
        subToolsLayout.setColor(color);
        subToolsLayout.setStrokeWidth((int) strokeWidth);
    }

    @Override
    public void onRedoUndoChanged(RedoUndoCount count) {
        redoUndoLayout.updateRedoUndoCount(count);
    }

    @Override
    public void onFastRoomCreated(FastRoom fastRoom) {
        this.fastRoom = fastRoom;
    }

    @Override
    public void attachSdk(FastSdk fastSdk) {
        this.fastSdk = fastSdk;
        fastSdk.registerObserver(this);
        scenesLayout.setFastSdk(fastSdk);
    }

    @Override
    public void updateFastStyle(FastStyle fastStyle) {
        subToolButton.setFastStyle(fastStyle);
        subToolsLayout.setFastStyle(fastStyle);

        toolButton.setFastStyle(fastStyle);
        toolsLayout.setFastStyle(fastStyle);

        redoUndoLayout.setFastStyle(fastStyle);
        scenesLayout.setFastStyle(fastStyle);
    }
}

package io.agora.board.fast;

import com.herewhite.sdk.domain.BroadcastState;
import com.herewhite.sdk.domain.MemberState;
import com.herewhite.sdk.domain.RoomPhase;

import io.agora.board.fast.model.FastStyle;
import io.agora.board.fast.model.RedoUndoCount;

/**
 * @author fenglibin
 */
public interface BoardStateObserver {
    default void onMemberStateChanged(MemberState memberState) {

    }

    default void onBroadcastStateChanged(BroadcastState broadcastState) {

    }

    default void onRoomPhaseChanged(RoomPhase roomPhase) {

    }

    default void onRedoUndoChanged(RedoUndoCount count) {

    }

    /**
     * 主题变更
     *
     * @param style
     */
    default void onGlobalStyleChanged(FastStyle style) {

    }
}

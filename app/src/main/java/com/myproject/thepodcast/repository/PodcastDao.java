package com.myproject.thepodcast.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RoomWarnings;
import androidx.room.Transaction;
import androidx.room.Update;

import com.myproject.thepodcast.model.DownloadedEpisode;
import com.myproject.thepodcast.model.FavouriteEpisode;
import com.myproject.thepodcast.model.HistoryEpisode;
import com.myproject.thepodcast.model.Podcast;

import java.util.List;


@Dao
public abstract class PodcastDao {


    @Query("SELECT * FROM favourite_episodes ORDER BY created_on DESC")
    public abstract List<FavouriteEpisode> listFavourites();

    @Query("SELECT * FROM downloaded_episodes ORDER BY created_on DESC")
    public abstract List<DownloadedEpisode> listDownloaded();

    @Query("DELETE FROM history_episodes WHERE uniqueId < :id")
    public abstract int clearHistory(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insertHistory(HistoryEpisode episode);

    @Transaction
    public long addHistory(HistoryEpisode episode) {
        List<HistoryEpisode> result = listHistoryForDelete();
        if (result != null && result.size() > 10) {
            clearHistory(result.get(9).getUniqueId());
        }
        return insertHistory(episode);
    }

    @Query("SELECT * FROM history_episodes ORDER BY uniqueId DESC LIMIT 1")
    public abstract HistoryEpisode getLatest();

    @Query("SELECT * FROM favourite_episodes WHERE id = :id")
    public abstract FavouriteEpisode getFavourite(final String id);

    @Query("SELECT * FROM history_episodes ORDER BY uniqueId DESC")
    public abstract List<HistoryEpisode> listHistory();

    @Query("SELECT * FROM history_episodes ORDER BY uniqueId DESC")
    public abstract List<HistoryEpisode> listHistoryForDelete();

    @Query("SELECT * FROM downloaded_episodes WHERE download_id = :requestId")
    public abstract DownloadedEpisode getDownloadedByRequestId(long requestId);

    @Query("SELECT * FROM downloaded_episodes WHERE id = :id")
    public abstract DownloadedEpisode getDownloadedById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long favourite(final FavouriteEpisode episode);

    @Delete
    public abstract int unFavourite(final FavouriteEpisode episode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long addDownload(final DownloadedEpisode episode);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract int updateDownload(final DownloadedEpisode episode);

    @Delete
    public abstract int removeFromDownloads(final DownloadedEpisode episode);

    @Query("SELECT * FROM podcasts ORDER BY created_on DESC")
    public abstract List<Podcast> listFollowingShows();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long follow(final Podcast podcast);

    @Delete
    public abstract int unFollow(final Podcast podcast);

    @Query("SELECT * FROM podcasts WHERE mId = :id")
    public abstract Podcast getFollowingShow(final @NonNull String id);

}

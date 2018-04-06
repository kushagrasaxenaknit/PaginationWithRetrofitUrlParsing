package  kushagra.com.paginationwithretrofiturlparsing.dataModel;

/**
 * Created by Kushagra Saxena on 19/03/2018.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Comparator;

public class OlaData implements Comparable<OlaData>{

    @SerializedName("song")
    @Expose
    private String song;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("artists")
    @Expose
    private String artists;
    @SerializedName("cover_image")
    @Expose
    private String coverImage;



    private boolean isFavourite;
    private int playFrequency;
    private int id;
    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getArtists() {
        return artists;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public int getPlayedCount() {
        return playFrequency;
    }

    public void setPlayedCount(int playedCount) {
        this.playFrequency = playedCount;
    }

    /**
     * Empty Constructor
     */
    public OlaData() {
    }

    /**
     * @param song       Song Name
     * @param url        Url of the song
     * @param artists    Artist Names
     * @param coverImage Cover Image of the Song
     */
    public OlaData(String song, String url, String artists, String coverImage) {
        this.song = song;
        this.url = url;
        this.artists = artists;
        this.coverImage = coverImage;
    }

    /**
     * @param id         Local index
     * @param song       Song Name
     * @param url        Url of the song
     * @param artists    Artist Names
     * @param coverImage Cover Image of the Song
     */
    public OlaData(int id, String song, String url, String artists, String coverImage) {
        this.id = id;
        this.song = song;
        this.url = url;
        this.artists = artists;
        this.coverImage = coverImage;
    }
    public int compareTo(OlaData compareFruit) {

        String compareSong = ((OlaData) compareFruit).getSong();


        //ascending order
        return this.song.compareTo(compareSong);


        //descending order
        //return compareQuantity - this.quantity;

    }


    public static Comparator<OlaData> OlaDataSongArtist
            = new Comparator<OlaData>() {

        public int compare(OlaData olaData1, OlaData olaData2) {
//decsen
            String song1 = olaData1.getSong();
            String song2 = olaData2.getSong();
            if(song1.compareTo(song2)==0)
            {
                String artists1=olaData1.getArtists();
                String artists2=olaData2.getArtists();
                //ascending order
                return artists1.compareTo(artists2);

            }
            else
            {
              return  song1.compareTo(song2);
            }

        }

    };

}
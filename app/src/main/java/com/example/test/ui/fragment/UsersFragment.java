package com.example.test.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.firestoreclass.FirestoreClass;
import com.example.test.models.User;
import com.example.test.ui.adapters.MyProductsListAdapter;
import com.example.test.ui.adapters.UsersListAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersFragment extends Fragment {


    UsersListAdapter adapterList;
    RecyclerView rv_my_user_items;
    //ListView lsvUser;
    //private ArrayAdapter<String> adapter;
    private ArrayList<User> userEmailList;

    private Map<String, String> userEmailIdkeyMap = new HashMap<>();

    private FirestoreClass firestoreClass;
    private int selectedPosition;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UsersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UsersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UsersFragment newInstance(String param1, String param2) {
        UsersFragment fragment = new UsersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        firestoreClass = new FirestoreClass();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_users, container, false);


        addControl(view);


//        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, userEmailList);
//        lsvUser.setAdapter(adapter);


        userEmailList = new ArrayList<>();
        rv_my_user_items.setVisibility(View.VISIBLE);
        for (User user : userEmailList) {
            Log.d("UsersFragment", "User: " + user.getEmail() + ", ID: " + user.getImage());
        }
        adapterList = new UsersListAdapter(requireActivity(), userEmailList, this);

        rv_my_user_items.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_my_user_items.setHasFixedSize(true);
        rv_my_user_items.setAdapter(adapterList);

        // Load user emails
        loadUserEmails();
        loadIdKeyEmail();





        //addEvent();

        return view;
    }

    void addControl(View view)
    {

        //lsvUser=view.findViewById(R.id.lsvUser);
        rv_my_user_items=view.findViewById(R.id.rv_my_user_items);

    }

//    void addEvent() {
//        lsvUser.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                String userEmailToDelete = userEmailList.get(position).getEmail();
//
//                selectedPosition = position;
//                new AlertDialog.Builder(requireContext())
//                        .setTitle("Xác nhận xóa")
//                        .setMessage("Bạn có chắc chắn muốn xóa người dùng này?")
//                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // Gọi hàm xóa người dùng
//                                String idKey=findIdKey(userEmailToDelete);
//                                String result = checkIdKey(idKey);
//                                deleteUser(userEmailToDelete,result);
//
//                            }
//                        })
//                        .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        })
//                        .show();
//
//                return true;
//            }
//        });
//    }

    private void loadUserEmails() {
        // Gọi hàm getUsersInfo từ FirestoreClass để lấy danh sách người dùng
        firestoreClass.getUsersInfo(users -> {
            // Sau khi nhận được danh sách người dùng, thêm email của từng người dùng vào danh sách userEmailList
            userEmailList.clear(); // Xóa danh sách trước khi thêm dữ liệu mới
            userEmailList.addAll(users); // Thêm người dùng vào danh sách
            adapterList.notifyDataSetChanged(); // Thông báo adapter về thay đổi dữ liệu
            Log.d("UsersFragment", "Emails loaded: " + users.size() + " users");
            for (User user : userEmailList) {
                Log.d("UsersFragment", "User: " + user.getEmail() + ", ID: " + user.getId());
            }
            return null;
        });
    }

    private void loadIdKeyEmail() {
        // Gọi hàm getUsersInfo từ FirestoreClass để lấy danh sách người dùng
        firestoreClass.getUsersInfo(users -> {
            for (User user : users) {
                userEmailIdkeyMap.put(user.getEmail(), user.getIdkey());
            }
            //adapter.notifyDataSetChanged(); // Thông báo cho adapter rằng danh sách đã thay đổi
            return null;
        });

    }

    private String findIdKey(String emailDelete) {
        //loadIdKeyEmail(); // Đảm bảo rằng userEmailIdkeyMap đã được tải

        // Duyệt qua userEmailIdkeyMap để tìm email và tương ứng với nó là idKey
        for (Map.Entry<String, String> entry : userEmailIdkeyMap.entrySet()) {
            String email = entry.getKey();
            String idKey = entry.getValue();
            if (email.equals(emailDelete)) {
                // Nếu tìm thấy email trùng khớp, trả về idKey tương ứng
                return idKey;
            }
        }

        // Nếu không tìm thấy email trùng khớp, trả về null hoặc giá trị mặc định phù hợp
        return null;
    }

    public  String processIDKeyNumber(String idKey) {
        // Khởi tạo StringBuilder để lưu chuỗi kết quả
        StringBuilder result = new StringBuilder();
        int segmentLength = 4;

        // Duyệt qua chuỗi theo từng đoạn có độ dài 4
        for (int i = 0; i < idKey.length(); i += segmentLength) {
            // Lấy đoạn hiện tại
            String segment = idKey.substring(i, Math.min(idKey.length(), i + segmentLength));

            // Giữ lại phần tử đầu tiên của đoạn hiện tại
            if (segment.length() > 0) {
                result.append(segment.charAt(0));
            }
        }

        // Chuyển kết quả thành chuỗi
        String resultString = result.toString();

        // Nếu chuỗi kết quả có nhiều hơn 3 phần tử, lấy 3 phần tử cuối đưa lên đầu
        if (resultString.length() > 3) {
            String lastThree = resultString.substring(resultString.length() - 3);
            resultString = lastThree + resultString.substring(0, resultString.length() - 3);
        }

        return resultString;
    }

    public  String processIDKeyLetter(String idKey) {
        // Khởi tạo StringBuilder để lưu chuỗi kết quả
        StringBuilder result = new StringBuilder();
        int segmentLength = 4;

        // Duyệt qua chuỗi theo từng đoạn có độ dài 4
        for (int i = 0; i < idKey.length(); i += segmentLength) {
            // Lấy đoạn hiện tại
            String segment = idKey.substring(i, Math.min(idKey.length(), i + segmentLength));

            // Giữ lại phần tử cuối cùng của đoạn hiện tại
            if (segment.length() > 0) {
                result.append(segment.charAt(3));
            }
        }

        // Chuyển kết quả thành chuỗi
        String resultString = result.toString();

        // Nếu chuỗi kết quả có nhiều hơn 3 phần tử, lấy 3 phần tử cuối đưa lên đầu
        if (resultString.length() > 3) {
            String lastThree = resultString.substring(resultString.length() - 3);
            resultString = lastThree + resultString.substring(0, resultString.length() - 3);
        }

        return resultString;
    }

    public  String checkIdKey(String idKey) {
        if (idKey.matches("\\d+")) {
            return processIDKeyNumber(idKey);
        } else if (idKey.matches("[a-zA-Z]+")) {
            return processIDKeyLetter(idKey);
        } else {
            return processIDKeyLetter(idKey);
        }
    }


    public void activeDeleteUser(String userEmailToDelete)
    {
        String idKey=findIdKey(userEmailToDelete);
        String result = checkIdKey(idKey);

        deleteUser(userEmailToDelete,result);
    }

    private void deleteUser(String userEmailToDelete,String idKey) {
        // Gọi hàm xóa người dùng từ FirestoreClass

        firestoreClass.deleteUserByEmail(userEmailToDelete,idKey, isSuccess -> {
            if (isSuccess) {
                // Xóa thành công, cập nhật giao diện
                userEmailList.remove(selectedPosition);
                adapterList = new UsersListAdapter(requireActivity(), userEmailList, this);

                rv_my_user_items.setLayoutManager(new LinearLayoutManager(getActivity()));
                rv_my_user_items.setHasFixedSize(true);
                rv_my_user_items.setAdapter(adapterList);
                Toast.makeText(requireContext(), "Xóa người dùng thành công", Toast.LENGTH_SHORT).show();
            } else {
                // Xóa thất bại, hiển thị thông báo
                Toast.makeText(requireContext(), "Xóa người dùng thất bại", Toast.LENGTH_SHORT).show();
            }
            return null;
        });
    }
}